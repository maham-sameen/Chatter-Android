package com.example.chatter.features.chats.presentation.conversation.model

import com.example.chatter.ui.ColorScheme
import com.example.chatter.ui.theme.blue400
import com.example.chatter.ui.theme.blue800

val dummyChatState = ChatState(
    name = "Alice Johnson",
    image = "https://example.com/alice_profile.jpg",
    lastSeen = "last seen just now",
    isOnline = true,
    isTyping = false,
    membersNames = "Alice, You",
    roomId = "room_001",
    colorScheme = ColorScheme(
        background = blue400,
        icon = blue800
    ),
    presenceText = ""
)

val messages = listOf(
    Message("0" , "Hey, how are you?", "7:00 PM", "Alice" , true , ""),
    Message("1" , "I'm good! Just finished my work.", "7:02 PM", "You",true  , ""),
    Message("2" , "Nice! What project are you working on?", "7:03 PM", "Alice",true , ""),
    Message("3", "Just a small Android app using Compose.", "7:05 PM", "You",true , " "),
    Message("4" , "Oh cool! I’ve heard Compose is fun.", "7:06 PM", "Alice",true , ""),
    Message("5" , "Yeah, it's really modern and easy once you get used to it.", "7:07 PM", "You",true , ""),
    Message("6" , "I should try it too sometime 😄", "7:08 PM", "Alice",true , ""),
    Message("7"  , "You definitely should!", "7:09 PM", "You",true , ""),
    Message("8" , "By the way, are you free this weekend?", "7:10 PM", "Alice",true , ""),
    Message("9" , "Maybe! What’s up?", "7:11 PM", "You",true , "") ,
    Message("10" , "Thinking of going hiking with some friends.", "7:12 PM", "Alice",true , ""),
    Message("11" , "Sounds awesome! Count me in.", "7:13 PM", "You",true , ""),
    Message("12" , "Perfect! I’ll send you the location later.", "7:14 PM", "Alice",true , ""),
    Message("13" , "Sure thing.", "7:15 PM", "You",true , ""),
    Message("14" , "Also, don’t forget your camera 📸", "7:16 PM", "Alice",true , ""),
    Message("15" , "Haha, never do 😂", "7:17 PM", "You",true , ""),
    Message("16" , "You always take the best pictures!", "7:18 PM", "Alice",true , ""),
    Message("17" , "Thanks! You’re pretty good at posing too 😄", "7:19 PM", "You",true , ""),
    Message("18" , "Haha stop, you’ll make me blush 😅", "7:20 PM", "Alice",true , ""),
    Message("19" , "Mission accomplished 😎", "7:21 PM", "You",true , ""),
    Message("20" , "Alright, talk later. Dinner time!", "7:22 PM", "Alice",true , ""),
    Message("21" , "Okay! Enjoy your food 🍽️", "7:23 PM", "You",true , "")
)