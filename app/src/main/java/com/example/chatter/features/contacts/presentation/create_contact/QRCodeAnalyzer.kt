//package com.example.chatter.features.contacts.presentation.create_contact
//
//import android.graphics.ImageFormat
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
//import com.google.zxing.BarcodeFormat
//import com.google.zxing.BinaryBitmap
//import com.google.zxing.DecodeHintType
//import com.google.zxing.MultiFormatReader
//import com.google.zxing.PlanarYUVLuminanceSource
//import com.google.zxing.common.HybridBinarizer
//import java.nio.ByteBuffer
//import kotlin.collections.arrayListOf
//
///**
// * It takes the callback in the constructor which will be called when we scan the QR code that gave
// * us a string that represent what QR code represents
// */
//class QRCodeAnalyzer (
// private val onQrCodeScanned : (String) -> Unit
//) : ImageAnalysis.Analyzer {
//
//    private val supportedImageFormats = listOf(
//        ImageFormat.YUV_420_888 ,
//        ImageFormat.YUV_422_888 ,
//        ImageFormat.YUV_444_888
//    )
//
//    /**
//     *
//     * ImageProxy represent the information about teh specific frame of our camera
//     * So we need to specify the list which specify which byte formate we support
//     * so we need to understand which formate we support in terms of bytes
//     */
//    override fun analyze(image: ImageProxy) {
//        /**
//         * we need to check if the formate of the scanned image proxy is in our supportedImageFormate
//         * then we execute our analyze function
//         * then we want the raw data of image proxy
//         *
//         * now we have raw bytes in this byte array ==> bytes
//         *
//         * then we have the planer luminous source so it is basically means lighting of our scenes
//         * now it takes many parameters first of all we have
//         * 1.Byte Array  , 2.data width  , 3.data height  , 4.left 5.top that in our case is zero so we start
//         * at very top to scan
//         * 6.
//         *
//         * now we can create the binary bitMap that will just be bit map file
//         * that contains information about bar code or rather QR code .
//         * you can also used to scan bar code but i can show how to use with QR code
//         *
//         *
//         * binaryBmp : contains information about scanned  QR code
//         *
//         * now we still have information but we need to decode to get the actual text .
//         * or actual info that QR code or bar code represent .
//         * we will do that in the try and catch block so any time if there is no QR code
//         * it will throw an exception
//         *
//         * MultiFormatReader that is class that can read different formate of QR code
//         * or bar code.
//         * setHints : we will map of all supported types of codes we would like to scan
//         */
//        if(image.format in supportedImageFormats){
//            val bytes = image.planes.first().buffer.toByteArray()
//            val source = PlanarYUVLuminanceSource(
//                bytes , image.width , image.height , 0 , 0 ,
//                image.width , image.height , false
//            )
//            val binaryBmp = BinaryBitmap(HybridBinarizer(source))
//            try {
//                val result = MultiFormatReader().apply {
//                    setHints(
//                        mapOf(
//                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
//                                BarcodeFormat.QR_CODE
//                            )
//                        )
//                    )
//                }.decode(binaryBmp)
//                onQrCodeScanned(result.text)
//            }catch (e : Exception){
//                e.printStackTrace()
//            }finally {
//                // we close image
//                image.close()
//            }
//        }
//    }
//
//    private fun ByteBuffer.toByteArray() : ByteArray{
//        /**
//         * rewind()  just make sure to start from beginning of our code of our byte buffer
//         *          remaining()  that is basically returns the size of how many bytes
//         *          we still have in the byte buffer
//         *          so by rewind() we make sure we start form beginning of byte buffer
//         *          so we make we include the whole byte buffer .
//         *          get() just make sure we get all these bytes into our byte array and we return that
//         */
//        rewind()
//        return ByteArray(remaining()).also { get(it) }
//    }
//}