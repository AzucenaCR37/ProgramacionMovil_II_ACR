package com.example.telefonia

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import android.telephony.SignalStrength
import android.telephony.SmsManager
import android.telephony.TelephonyCallback.ServiceStateListener
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.telefonia.vm.ViewModelSMS

class BroadcastReceiver: BroadcastReceiver() {
    private var listener: ServiceStateListener? = null
    private var telephonyManager:TelephonyManager?=null
    private var context1:Context?= null

    override fun onReceive(context2: Context, intent:Intent){
        val action=intent.action
        context1=context2

        if (action==TelephonyManager.ACTION_PHONE_STATE_CHANGED){
            telephonyManager=context2.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var getNumber=intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            var realNumber= ShareViewModel.viewModel.phone

            if(getNumber!=null && getNumber==realNumber){
                sendMessage(number = getNumber)
            }

            Toast.makeText(context1, "¡Registered receiver!", Toast.LENGTH_LONG).show()
            listener=ServiceStateListener()
            telephonyManager?.listen(listener,PhoneStateListener.LISTEN_SERVICE_STATE)
            telephonyManager?.listen(listener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        }
    }

    private  fun sendMessage(
        number:String){
        val sms=SmsManager.getDefault()
        val msg=ShareViewModel.viewModel.sms
        sms.sendTextMessage(number,null,msg,null,null)
    }

    private inner class ServiceStateListener:PhoneStateListener(){
        override fun onServiceStateChanged(serviceState: ServiceState) {
            super.onServiceStateChanged(serviceState)
            val conected=  serviceState.state==ServiceState.STATE_IN_SERVICE
            if(conected){
                Toast.makeText(context1, "¡Established connection!", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context1, "¡Lost connection!", Toast.LENGTH_LONG).show()
            }
        }

        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
            super.onSignalStrengthsChanged(signalStrength)
            Toast.makeText(
                context1,
                "Changed signal - CDMA: ${signalStrength.cdmaDbm} GSM: ${signalStrength.gsmSignalStrength}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}