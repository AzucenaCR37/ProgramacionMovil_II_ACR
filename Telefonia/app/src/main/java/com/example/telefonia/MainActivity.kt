package com.example.telefonia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.telefonia.ui.theme.TelefoniaTheme
import com.example.telefonia.vm.ViewModelSMS

class MainActivity : ComponentActivity() {
    lateinit var  view:ViewModelSMS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelefoniaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                    view=ViewModelProvider(this).get(ViewModelSMS::class.java)
                }
            }
        }
    }
}


@Composable
fun Greeting(modifier: Modifier = Modifier,
             viewModel: ViewModelSMS = viewModel (factory= ViewModelSMS.Factory )) {

    SharedViewModel.viewModel=viewModel

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(40.dp)
            .fillMaxWidth()
    ){

        Spacer(modifier = Modifier.height(150.dp))
        Row(
        ){

            Text(
                text= "Automatic SMS Telephony",
                fontSize = 21.sp,
                fontFamily =  FontFamily.SansSerif,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        OutlinedTextField(
            value = viewModel.phone,
            label = { Text(text="Type the phone number")},
            onValueChange ={
                viewModel.phoneUpdate(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier= Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(25.dp))
        OutlinedTextField(
            value = viewModel.sms,
            label = { Text(text="Type the SMS")},
            onValueChange ={
                viewModel.smsUpdate(it)
            },
            modifier= Modifier.fillMaxWidth()
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TelefoniaTheme {
        Greeting()
    }
}


object  SharedViewModel{
    lateinit var  viewModel:ViewModelSMS
}