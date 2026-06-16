package com.example.chatter.core.data.data_source.local

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.chatter.core.data.data_source.model.local.User
import com.example.chatter.core.data.data_source.model.local.UserPreference
import com.example.chatter.core.data.data_source.utils.UserSerializer
import com.example.chatter.core.data.data_source.utils.encrypted
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlin.toString


public class UserDataSource constructor(
     val dataStore : DataStore<UserPreference?>
) : DataStore<UserPreference?> by dataStore {

    /**
     * Update user preferences with the give [UserPreferences].
     *
     * @param UserPreferences A new [UserPreferences] to replace previous persisted data.
     */
    public suspend fun updateUserPreferences(userPreferences: UserPreference) {
        println("\uD83E\uDD70 UserDataSource updateUserPreference UserPreference : ${userPreferences.toString()}")
        updateData { userPreferences }
    }



    /**
     * Update [User] information that is used to build a `StreamVideo` instance for logging in.
     *
     * @param user A user instance to be used logged in.
     */
    public suspend fun updateUser(user: User?) {
        println("\uD83E\uDD70  UserDataSource updateUser , user : ${user.toString()}")
        updateData { preferences ->
            preferences?.copy(user = user)
        }
    }


    public suspend fun updateUserProfile(image : String){
        println("\uD83E\uDD70  UserDataSource updateUserProfile , user: ${user.toString()}")
        updateData { preferences ->
            preferences?.copy(
                user = preferences.user?.copy(image = image)
            )
        }
    }


    /**
     * Update [UserToken] information that is used to build a `StreamVideo` instance for logging in.
     *
     * @param userToken An user token instance to be used logged in.
     */
    public suspend fun updateUserToken(userToken: String) {
        println("\uD83E\uDD70 UserDataSource updateUserToken , userToken : ${userToken.toString()}")
        updateData { preferences ->
            preferences?.copy(userToken = userToken)
        }
    }

    /** Clear the persisted all user data. */
    public suspend fun clear(): UserPreference? = dataStore.updateData { null }


    /**
     * Returns `true` if there is no usable user token persisted, i.e. the user
     * either was never signed in or has logged out. Treated as the local
     * "session expired" signal used at app launch to decide between the auth
     * and home graphs without any network round-trip.
     */
    public suspend fun isTokenExpired(): Boolean {
        val pref = data.firstOrNull()
        val expired = pref?.userToken.isNullOrBlank()
        println("🥰 UserDataSource isTokenExpired -> $expired (userToken blank? $expired)")
        return expired
    }


    /** A state that contains a persisted [User] data. */
    public val user: Flow<User?> = data.map {
        println("\uD83E\uDD70 UserDataStore , user : ${it?.toString()}")
        it?.user
    }


    /** A state that contains a persisted [UserToken] data. */
    public val userToken: Flow<String> =
        data.map {
            println("\uD83E\uDD70 UserDataStore , apiKey : ${it?.toString()}")
            it?.userToken.orEmpty() }


    public companion object {
        private const val MASTER_KEY_PREFERENCE_FILE = "master_key_preference"
        private const val USER_PROTO_FILE = "proto_chatter_user.pb"
        private const val MASTER_KEY_NAME = "master_key"
        /**
         * Represents if [UserDataStore] is already installed or not.
         * Lets you know if the internal [UserDataStore] instance is being used as the
         * uncaught exception handler when true or if it is using the default one if false.
         */
        public var isInstalled: Boolean = false
            get() = internalUserDataStore != null
            private set

        /**
         * [UserDataStore] instance to be used.
         */
        @Volatile
        private var internalUserDataStore: UserDataSource? = null

        /**
         * Returns an installed [UserDataStore] instance or throw an exception if its not installed.
         */
        public fun instance(): UserDataSource {
            return internalUserDataStore
                ?: throw IllegalStateException(
                    "UserDataStore.install() must be called before obtaining StreamUserDataStore instance.",
                )
        }

        /**
         * Installs a new [StreamUserDataStore] instance to be used.
         *
         *
         * No problem bro 😎 — let’s reset and go **super simple**, like a story.
         * I’ll explain what’s happening step by step — no heavy words.
         *
         * ---
         *
         * ## 🔐 Imagine this:
         *
         * You bought a **safe box** 🏦 (this is **Tink** — a security library).
         * You want to **lock** and **unlock** important data, like your passwords or tokens.
         *
         * But before using the safe, you have to:
         *
         * * Install the **lock system** (AES, AEAD, etc.)
         * * Tell the safe which **key** you’ll use
         *
         * That’s what all this `AeadConfig.register()` and `Registry` stuff is doing.
         * It’s setting up the “security system” before you can store or open data.
         *
         * ---
         *
         * ## 🧩 Step-by-step breakdown
         *
         * ### 🥇 Step 1 — What is “Tink”
         *
         * Tink is a **Google security library** that helps you:
         *
         * * Encrypt data (make it unreadable to others)
         * * Decrypt data (read it again safely)
         * * Handle encryption keys automatically
         *
         * You don’t have to do complex cryptography — Tink does it all.
         *
         * ---
         *
         * ### 🥈 Step 2 — What is “Aead”
         *
         * `Aead` = **Authenticated Encryption with Associated Data**
         * 👉 Just think of it as a **stronger lock** 🔒
         *
         * It not only hides your data but also checks:
         *
         * > “Has anyone changed or hacked this data?”
         *
         * If someone tampers with it, Tink will refuse to unlock it.
         *
         * ---
         *
         * ### 🥉 Step 3 — What is `AeadConfig.register()`
         *
         * This line:
         *
         * ```kotlin
         * AeadConfig.register()
         * ```
         *
         * tells Tink:
         *
         * > “Load all the AEAD lock types so I can use them.”
         *
         * You **must** do this one time when your app starts.
         * Otherwise, Tink won’t know what kind of locks to use — and encryption will fail.
         *
         * ---
         *
         * ### 🏗️ Step 4 — What is “Registry”
         *
         * The **Registry** is like a **toolbox list** inside Tink.
         *
         * When you call `AeadConfig.register()`,
         * Tink adds all AEAD tools into that toolbox so you can pick one later.
         *
         * Example:
         *
         * ```
         * Registry now contains:
         * - AES256_GCM
         * - ChaCha20_Poly1305
         * - AES128_GCM
         * ```
         *
         * Now you can say:
         *
         * > “Hey Tink, give me the AES256_GCM lock!”
         * > And Tink knows what that means because it’s registered.
         *
         * ---
         *
         * ### 🧰 Step 5 — What happens next
         *
         * Once registered, you can:
         *
         * ```kotlin
         * val keysetHandle = ... // your encryption key
         * val aead = keysetHandle.getPrimitive(Aead::class.java)
         * ```
         *
         * Now `aead` is like your **personal lock and key** 🗝️
         * You can use it to:
         *
         * ```kotlin
         * val encrypted = aead.encrypt("hello".toByteArray(), null)
         * val decrypted = aead.decrypt(encrypted, null)
         * ```
         *
         * ---
         *
         * ### 🧠 Summary (super simple)
         *
         * | Concept                   | Simple meaning                                                           |
         * | ------------------------- | ------------------------------------------------------------------------ |
         * | **Tink**                  | A library that helps you lock (encrypt) and unlock (decrypt) data safely |
         * | **Aead**                  | A very strong type of lock                                               |
         * | **Registry**              | A list where Tink keeps all available lock types                         |
         * | **AeadConfig.register()** | Loads all AEAD lock types into that list                                 |
         * | **KeysetHandle**          | Holds your secret keys                                                   |
         * | **Aead primitive**        | The actual lock object used for encryption and decryption                |
         *
         * ---
         *
         * So, in one line bro 👇
         *
         * > **`AeadConfig.register()` is just setting up Tink’s lock system (AEAD) so you can later encrypt and decrypt your app’s data safely.**
         *
         * ---
         *
         * Would you like me to show you a **small real example** in Kotlin
         * where we encrypt and decrypt a simple string using Tink?
         * That will make everything crystal clear 🔥
         *
         *
         * No worries bro 😎
         * Let’s go **super slow**, **super simple**, like you’re hearing it for the first time ever.
         * We’ll break it down like a story — and by the end, it’ll be *crystal clear*. 💡
         *
         * ---
         *
         * ## 🧠 Step 1 — What is “AEAD”?
         *
         * **AEAD** means
         * 👉 *Authenticated Encryption with Associated Data*
         *
         * Forget the big name.
         * Here’s what it really means:
         *
         * ### 🔒 Normally:
         *
         * When you **encrypt** something (like a password),
         * it just hides it — nobody can read it.
         *
         * But… a hacker could still *change it* (even if they can’t read it).
         * So you’d never know if your data was tampered with.
         *
         * ---
         *
         * ### ❤️ With AEAD:
         *
         * It hides the data **AND** checks if someone changed it.
         *
         * So AEAD does **two things at once**:
         *
         * 1. Keeps your data secret (encryption 🔐)
         * 2. Checks if it was changed (authentication ✅)
         *
         * That’s why it’s called **Authenticated Encryption**.
         *
         * ---
         *
         * ## 💬 Example
         *
         * Let’s say your app saves this message:
         *
         * ```
         * "My password is 1234"
         * ```
         *
         * When AEAD encrypts it, you get something unreadable like:
         *
         * ```
         * b91#@3Jxk!@F29...
         * ```
         *
         * Now if a hacker changes even one letter,
         * AEAD will know when you try to decrypt it —
         * and it will **throw an error** ❌
         * because the message was changed.
         *
         * That’s how it keeps your data safe and honest. 💪
         *
         * ---
         *
         * ## ⚙️ Step 2 — What is `AeadConfig.register()`?
         *
         * Before you can use AEAD, you must tell Tink:
         *
         * > “Hey, I want to use AEAD encryption. Please get everything ready.”
         *
         * That’s what this line does:
         *
         * ```kotlin
         * AeadConfig.register()
         * ```
         *
         * It’s like saying:
         *
         * > “Load all the tools that AEAD needs.”
         *
         * ---
         *
         * ## 🧰 Step 3 — What happens inside when you register?
         *
         * Inside Tink, there’s something called a **Registry**.
         * Think of it like a *toolbox list* 🧰.
         *
         * When you call `AeadConfig.register()`,
         * Tink puts all AEAD tools into that toolbox so it can use them later.
         *
         * If you **don’t** call it,
         * then when you try to encrypt, Tink will say:
         *
         * > “Bro, I don’t know what AEAD is!” 😅
         *
         * ---
         *
         * ## 🔗 Step 4 — AEAD also depends on MAC
         *
         * AEAD uses another system under the hood called **MAC**
         * (“Message Authentication Code”).
         *
         * MAC = a small tag that proves your data wasn’t changed.
         *
         * So when you call:
         *
         * ```kotlin
         * AeadConfig.register()
         * ```
         *
         * Tink automatically also sets up the **MAC tools**,
         * because AEAD can’t work without them.
         *
         * ---
         *
         * ## ⚠️ Step 5 — What does “throws GeneralSecurityException” mean?
         *
         * If something goes wrong during setup (like missing keys, bad files, etc.),
         * Tink throws this:
         *
         * ```kotlin
         * GeneralSecurityException
         * ```
         *
         * It just means “Something went wrong with your security setup.”
         *
         * ---
         *
         * ## 🧩 Step 6 — Summary in your words, bro:
         *
         * | Concept                      | Simple Meaning                                                       |
         * | ---------------------------- | -------------------------------------------------------------------- |
         * | **AEAD**                     | A strong kind of encryption that also checks if the data was changed |
         * | **AeadConfig.register()**    | Tells Tink to prepare all AEAD (and MAC) tools before use            |
         * | **Registry**                 | A list inside Tink that remembers which tools are ready              |
         * | **MAC**                      | A secret code used to check data wasn’t changed                      |
         * | **GeneralSecurityException** | Error when something goes wrong in setup                             |
         *
         * ---
         *
         * ## 🧠 Super Simple Analogy
         *
         * Think of AEAD like a **sealed envelope** ✉️:
         *
         * * The letter inside is **encrypted** (hidden).
         * * The seal outside is the **authentication code (MAC)**.
         *
         * If someone opens or changes the letter,
         * the seal breaks — and AEAD refuses to open it again.
         *
         * That’s how it keeps your message **private and safe**. 🔐✅
         *
         * ---
         *
         * Would you like me to show you a **tiny real example** in Kotlin (10 lines max)
         * so you can see exactly how it encrypts and decrypts text using AEAD?
         * It’ll make the idea instantly clear.
         *
         */
        public fun install(
            context: Context,
            isEncrypted: Boolean = true,
        ): UserDataSource {
            synchronized(this) {
                println("\uD83E\uDD70 UserDataSource install")
                if (isInstalled) {
                    println("\uD83E\uDD70 UserDataSource isInstalled")
                    return internalUserDataStore!!
                }

                val dataStore = if (isEncrypted) {
                    println("\uD83E\uDD70 UserDataSource isEncrypted")
                    AeadConfig.register()

                    val aead = try {
                        createAead(context)
                    } catch (e: Exception) {
                        // The client application is most likely using allowBack="true" in manifest
                        // and our encrypted datastore can't be restored. We need to clear it.
                        println("\uD83E\uDD70 UserDataSource install : ${e.toString()}")
                        deleteEncryptionKeysAndData(context)
                        createAead(context)
                    }
                    DataStoreFactory.create(serializer = UserSerializer().encrypted(aead)) {
                        context.dataStoreFile(USER_PROTO_FILE)
                    }
                } else {
                    DataStoreFactory.create(serializer = UserSerializer()) {
                        context.dataStoreFile(USER_PROTO_FILE)
                    }
                }

                val userDataStore = UserDataSource(dataStore)

                internalUserDataStore = userDataStore
                return userDataStore
            }
        }

        /**
         *
         * ---
         *
         * ### 🧠 What it is
         *
         * `AndroidKeysetManager` is part of **Google Tink**, a security library that helps you **encrypt and decrypt data safely** in Android apps.
         *
         * Think of it as a **key manager** that helps your app:
         *
         * * Create,
         * * Store,
         * * and Load **encryption keys** automatically — in a secure way.
         *
         * ---
         *
         * ### 🏠 Where it stores the keys
         *
         * It saves your encryption keys inside **SharedPreferences** — but **not as plain text**.
         * It uses an encrypted format so others can’t read it.
         *
         * > ⚠️ Because SharedPreferences involves disk access, it should **not** be done on the **UI thread** (main thread), or your app might freeze.
         *
         * ---
         *
         * ### 💡 Why it’s needed
         *
         * Every time your app encrypts or decrypts something (like files, messages, tokens, etc.), it needs a **key**.
         * Instead of creating and managing keys manually (which is error-prone), you let `AndroidKeysetManager` handle it automatically and safely.
         *
         * ---
         *
         * ### 🛠 How it works step by step
         *
         * #### Step 1: You build it
         *
         * You use a **Builder pattern** to set it up once — typically when your app starts:
         *
         * ```java
         * AndroidKeysetManager manager = new AndroidKeysetManager.Builder()
         *     .withSharedPref(getApplicationContext(), "my_keyset_name", "my_pref_file_name")
         *     .withKeyTemplate(KeyTemplates.get("AES128_GCM_HKDF_4KB"))
         *     .build();
         * ```
         *
         * Here’s what each part means:
         *
         * | Function                                                          | Meaning                                                                                                                                  |
         * | ----------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
         * | `.withSharedPref(context, "my_keyset_name", "my_pref_file_name")` | Says: "Store the key inside SharedPreferences file named `my_pref_file_name`, under the key name `my_keyset_name`."                      |
         * | `.withKeyTemplate(KeyTemplates.get("AES128_GCM_HKDF_4KB"))`       | Says: "If no key exists yet, create a new encryption key using this template." (Here it’s an AES key suitable for streaming encryption.) |
         * | `.build()`                                                        | Finishes building the manager.                                                                                                           |
         *
         * ---
         *
         * #### Step 2: The manager looks for a key
         *
         * When you call `.build()`:
         *
         * * It first **checks** if a key already exists in SharedPreferences.
         *
         *   * ✅ **If found** → It **loads** it and uses it.
         *   * ❌ **If not found** → It **creates** a new one using the `KeyTemplate` you provided, and **saves it** there.
         *
         * ---
         *
         * #### Step 3: You use it to encrypt/decrypt
         *
         * After setup, you can get the actual encryption object:
         *
         * ```java
         * StreamingAead streamingAead = manager.getKeysetHandle().getPrimitive(StreamingAead.class);
         * ```
         *
         * Now you can use `streamingAead` to **encrypt** and **decrypt** data streams securely.
         *
         * ---
         *
         * ### ⚠️ Errors and what they mean
         *
         * | Type of error              | When it happens                                    | What it means                                                                                                                                                                                       |
         * | -------------------------- | -------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
         * | `IOException`              | Problem reading/writing the SharedPreferences file | Maybe the file is corrupt or inaccessible                                                                                                                                                           |
         * | `GeneralSecurityException` | The keyset exists, but cannot be decrypted         | Usually means the **master key** used to encrypt the keyset has changed or is missing — this is **irrecoverable**. You’ll need to **delete the old keyset** and **re-encrypt data** with a new one. |
         *
         * ---
         *
         * ### 🧩 In short
         *
         * | Step | What happens                                                                          |
         * | ---- | ------------------------------------------------------------------------------------- |
         * | 1️⃣  | You create an `AndroidKeysetManager` and tell it where to store your encryption key   |
         * | 2️⃣  | It checks SharedPreferences for an existing key                                       |
         * | 3️⃣  | If no key found, it makes a new one and saves it                                      |
         * | 4️⃣  | You use that key (via `StreamingAead`) to encrypt/decrypt data                        |
         * | 5️⃣  | If the key can’t be read later (wrong master key), you must delete it and start fresh |
         *
         * ---
         *
         * Perfect bro 😎 — let’s break this down step by step so **everything about `withMasterKeyUri()` and `KeysetHandle`** becomes clear and easy to understand.
         *
         * ---
         *
         * ## 🧩 Part 1 — `withMasterKeyUri(String val)`
         *
         * ### 💬 What it does
         *
         * This method tells the `AndroidKeysetManager` **which master key** to use to **encrypt your app’s keyset** (the keys you use for encryption/decryption).
         *
         * ---
         *
         * ### 🧠 Think of it like this
         *
         * You have **two levels of keys**:
         *
         * | Level          | What it does                                   | Where it’s stored                                                |
         * | -------------- | ---------------------------------------------- | ---------------------------------------------------------------- |
         * | **Master key** | Encrypts and protects your app’s keyset        | Stored in **Android Keystore** (secure chip area)                |
         * | **App keyset** | Actually encrypts and decrypts your app’s data | Stored in **SharedPreferences**, but encrypted by the master key |
         *
         * So:
         * 👉 The **master key** is like a **big lock** that protects your **smaller locks (keys)** inside SharedPreferences.
         *
         * ---
         *
         * ### 🪄 Example
         *
         * ```java
         * AndroidKeysetManager manager = new AndroidKeysetManager.Builder()
         *     .withSharedPref(getApplicationContext(), "my_keyset_name", "my_pref_file_name")
         *     .withKeyTemplate(KeyTemplates.get("AES128_GCM_HKDF_4KB"))
         *     .withMasterKeyUri("android-keystore://my_master_key")
         *     .build();
         * ```
         *
         * Here:
         *
         * * `"android-keystore://my_master_key"` tells Tink to look for a **master key** stored securely in the **Android Keystore** system.
         * * That master key will be used to **encrypt your keyset**, so even if someone accesses your SharedPreferences file, they can’t read your encryption keys.
         *
         * ---
         *
         * ### ⚙️ Important details
         *
         * | Concept              | Meaning                                                                                                                                       |
         * | -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
         * | **Android Keystore** | A special secure system area in Android (hardware-backed on most phones) that stores encryption keys safely so apps can’t directly read them. |
         * | **URI format**       | Must start with `android-keystore://` (this tells Tink you’re using the Android Keystore system).                                             |
         * | **API requirement**  | Works only on Android M (API 23) or higher. On older Android versions, calling it doesn’t cause an error, but it just **does nothing**.       |
         * | **Why use it**       | Protects your keys even if SharedPreferences is copied or your app data is leaked.                                                            |
         *
         * ---
         *
         * ## 🔐 Part 2 — `KeysetHandle`
         *
         * ### 💬 What it is
         *
         * `KeysetHandle` is basically a **secure reference** to your keyset — not the actual keys themselves.
         *
         * It’s like holding the **handle of a locked box**, not the keys inside it.
         *
         * ---
         *
         * ### 💡 Why it exists
         *
         * To **limit exposure** of your sensitive key material.
         * You don’t want to ever hold raw encryption keys directly in memory, because:
         *
         * * They might leak in logs or crashes.
         * * They could be read by malicious code.
         *
         * So, Tink gives you a **`KeysetHandle`**, which internally knows how to access the keyset **without exposing it**.
         *
         * ---
         *
         * ### 🧠 Analogy
         *
         * Imagine:
         *
         * * The **keyset** = a box full of your encryption keys.
         * * The **KeysetHandle** = a handle on that box that lets you *use* the keys (encrypt/decrypt), but not *open* the box to see the keys.
         *
         * ---
         *
         * ### 🪄 Example usage
         *
         * Once you have a manager:
         *
         * ```java
         * KeysetHandle handle = manager.getKeysetHandle();
         * ```
         *
         * You can then get an encryption primitive:
         *
         * ```java
         * StreamingAead aead = handle.getPrimitive(StreamingAead.class);
         * ```
         *
         * Here:
         *
         * * You’re saying: “Give me an encryption tool that can use this keyset.”
         * * But you never directly touch or see the raw key material.
         *
         * ---
         *
         * ### 🚪 Different KeysetHandle types
         *
         * | Type                        | Purpose                                                                                              |
         * | --------------------------- | ---------------------------------------------------------------------------------------------------- |
         * | **`KeysetHandle`**          | Normal handle (usually encrypted). Used with `AndroidKeysetManager`.                                 |
         * | **`CleartextKeysetHandle`** | Dangerous version that lets you read/write **unencrypted** keysets. Only for testing or special use. |
         * | **`NoSecretKeysetHandle`**  | Used when your keyset doesn’t contain secret material (for public keys, etc.).                       |
         *
         * ---
         *
         * ## 🧠 Summary
         *
         * | Concept                   | Meaning                                                                                                |
         * | ------------------------- | ------------------------------------------------------------------------------------------------------ |
         * | **`withMasterKeyUri()`**  | Tells Tink which secure key (in Android Keystore) to use to protect your keyset.                       |
         * | **Master key**            | Encrypts your app’s keyset before saving it to SharedPreferences.                                      |
         * | **KeysetHandle**          | A secure handle that lets you *use* keys (encrypt/decrypt) without ever exposing the actual keys.      |
         * | **Why this is important** | Keeps encryption keys safe, even if app data leaks — the actual keys stay encrypted by the master key. |
         *
         * ---
         *
         *
         */
        private fun createAead(context: Context) =
            AndroidKeysetManager.Builder()
                .withSharedPref(context, "master_keyset",
                    MASTER_KEY_PREFERENCE_FILE
                )
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri("android-keystore://${MASTER_KEY_NAME}")
                .build()
                .keysetHandle
                .getPrimitive(Aead::class.java)

        private fun deleteEncryptionKeysAndData(context: Context) {
            println("\uD83E\uDD70 UserDataSource deleteEncryptionKeysAndData e")
            // Remove the preference file
            context.getSharedPreferences(MASTER_KEY_PREFERENCE_FILE, Context.MODE_PRIVATE)
                .edit {
                    clear()
                }

            // Delete the master key
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            try {
                keyStore.load(null)
                keyStore.deleteEntry(MASTER_KEY_NAME)
            } catch (e: Exception) {
               // Let's try to proceed anyway
                println("\uD83E\uDD70 UserDataSource deleteEncryptionKeysAndData e : ${e.toString()}")
            }

            // Delete stored data
            context.dataStoreFile(USER_PROTO_FILE).delete()
        }

        /**
         * Uninstall a previous [UserDataStore] instance.
         */
        public fun unInstall() {
            internalUserDataStore = null
        }
    }
}


/**
 * Exactly bro! 😎 Let me clarify carefully.
 *
 * ---
 *
 * ### 1️⃣ **DataStore supports two main types of storage**:
 *
 * #### **a) Proto DataStore**
 *
 * * Stores **Protobuf messages**.
 * * Requires you to use `.proto` files → generates classes like `Person`.
 * * Uses a `Serializer<T>` (like your `DataSerializer`) to read/write.
 * * Pros: efficient, cross-platform, strongly typed.
 * * Cons: only works with Protobuf messages.
 *
 * #### **b) Preferences DataStore**
 *
 * * Stores **key-value pairs** (like SharedPreferences) — limited types (String, Int, Boolean, etc.).
 * * Not suitable for complex objects.
 *
 * #### **c) Kotlinx Serialization + DataStore**
 *
 * * There’s no “official” DataStore type for arbitrary Kotlin objects, but you **can** make it work using a custom `Serializer<T>` like your `UserSerializer`.
 * * Basically, you create a `Serializer<MyKotlinClass>` that uses `ProtoBuf.encodeToByteArray` / `decodeFromByteArray`.
 * * Then you pass it to `dataStore()` in the same way as Protobuf DataStore.
 *
 * So yes, bro: you **can store Kotlin `@Serializable` classes** in a ProtoBuf-backed DataStore — you’re just using Kotlinx Serialization to produce the bytes instead of the Protobuf-generated code.
 *
 * ---
 *
 * ### Example:
 *
 * ```kotlin
 * @Serializable
 * data class UserPref(val name: String, val age: Int)
 *
 * val Context.userDataStore by dataStore(
 *     fileName = "user_prefs.pb",
 *     serializer = object : Serializer<UserPref> {
 *         override val defaultValue = UserPref("", 0)
 *
 *         override suspend fun readFrom(input: InputStream): UserPref {
 *             val bytes = input.readBytes()
 *             return ProtoBuf.decodeFromByteArray(UserPref.serializer(), bytes)
 *         }
 *
 *         override suspend fun writeTo(t: UserPref, output: OutputStream) {
 *             val bytes = ProtoBuf.encodeToByteArray(UserPref.serializer(), t)
 *             output.write(bytes)
 *         }
 *     }
 * )
 * ```
 *
 * * Works like Proto DataStore, but you can store **any Kotlinx Serializable class**, not just Protobuf messages.
 *
 * ---
 *
 * 💡 **Rule of thumb bro:**
 *
 * * Already using `.proto` files → use **native Protobuf** (`DataSerializer`).
 * * Pure Kotlin classes → use **Kotlinx Serialization** (`UserSerializer`).
 *
 * ---
 *
 * If you want, I can show a **side-by-side setup** with both `Person` (Protobuf) and a Kotlin class in the **same project** so you see how DataStore handles both.
 *
 * Do you want me to do that?
 *
 */