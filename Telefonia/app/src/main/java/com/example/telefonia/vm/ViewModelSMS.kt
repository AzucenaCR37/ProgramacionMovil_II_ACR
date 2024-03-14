package com.example.telefonia.vm

import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class ViewModelSMS():ViewModel(){
    var phone by mutableStateOf("")

    fun phoneUpdate(number: String){
        phone=number
    }

    var sms by mutableStateOf("")

    fun smsUpdate(msg: String){
        sms=msg
    }


    companion object{
        val Factory= viewModelFactory {
            initializer {
                ViewModelSMS()
            }
        }
    }
}