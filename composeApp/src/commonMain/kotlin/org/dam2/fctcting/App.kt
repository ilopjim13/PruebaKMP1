package org.dam2.fctcting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.es.appmovil.viewmodel.UserViewModel
import org.dam2.fctcting.screens.LoginScreen
import org.dam2.fctcting.screens.ResumeScreen
import org.dam2.fctcting.viewmodels.DataViewModel
import org.dam2.fctcting.viewmodels.DataViewModel.getMonth
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import pruebakmp2.composeapp.generated.resources.Res
import pruebakmp2.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
//    MaterialTheme {
//        var showContent by remember { mutableStateOf(false) }
//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }
//    }
    MaterialTheme {
        val userViewmodel = UserViewModel()
        DataViewModel
        getMonth()

        Navigator(screen = LoginScreen(userViewmodel))
//        { navigator: Navigator ->
//            SlideTransition(navigator)
//        }
    }
}