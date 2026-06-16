//package com.example.chatter.features.contacts.presentation.create_contact
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.util.Size
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.compose.LocalLifecycleOwner
//
//
///**
// * How do we render camera preview in the jetpack compose
// * In factory we have access to the context with that context we render our camera
// * that is so called preview view .
// *
// * now we want to load the camera image
// * now we have the selector we select the camera tha we want to show either front or back camera
// * CameraSelector.LENS_FACING_BACK ==> means we are selecting teh back camera
// * now the by selector preview know which camera it should render
// *
// *
// * * this  analyze() function will be called for every single frame of our camera
// *      * so what if this analyze() is slower than frames actually comings in.
// *      * so if we get 60 frames per second but the analyzer() only be executed 30 times per seconds .
// *      * so backPressureStrategy tells what should in that case
// *      ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST ++> it will only keep the latest frame so
// *      if the previous frame is still processing we get a new one then it will drop
// *      the previous one .
// */
//@Composable
//fun QRCodeScanner(){
//    var code by  remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val cameraProviderFeature = remember {
//        ProcessCameraProvider.getInstance(context)
//    }
//
//    var hasCamPermissions by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context ,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = {
//            hasCamPermissions = it
//        }
//    )
//    LaunchedEffect(true) {
//        launcher.launch(Manifest.permission.CAMERA)
//    }
//
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    Column (
//        modifier = Modifier.fillMaxSize()
//    ){
//        if(hasCamPermissions){
//            AndroidView(
//                factory = { context ->
//                    val previewView = PreviewView(context)
//                    val preview = Preview.Builder().build()
//                    val selector = CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                        .build()
//                    preview.setSurfaceProvider(previewView.surfaceProvider)
//
//                    // now we need to analyze the image
//                    val imageAnalysis = ImageAnalysis.Builder()
//                        .setTargetResolution(
//                            Size(
//                                640,
//                                480
//                            )
//                        )
//                        .setBackpressureStrategy(
//                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
//                        ).build()
//                    imageAnalysis.setAnalyzer(
//                        ContextCompat.getMainExecutor(context),
//                        QRCodeAnalyzer {
//                            code = it
//                        }
//                    )
//                    try {
//                        cameraProviderFeature.get().bindToLifecycle(
//                            lifecycleOwner, selector, preview, imageAnalysis
//                        )
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    previewView
//                }
//            )
//            Text(text = code , fontSize = 20.sp , modifier = Modifier.fillMaxWidth().padding(20.dp))
//        }
//    }
//}