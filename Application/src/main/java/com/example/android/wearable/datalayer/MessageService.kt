package com.example.android.wearable.datalayer

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MessageService : WearableListenerService() {

    override fun onMessageReceived(message : MessageEvent) {
        Log.d("Test", "New message: $message")
        try {
            if(message.path == BuildConfig.PATH_COMMAND) {
                startActivity(
                    Intent(Intent.ACTION_VIEW).also {
                        it.data = Uri.parse(String(message.data))
                        it.addFlags(FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            }
        } catch (t : Throwable) {
            Log.e("Test", "Some things went wrong", t)
        }
    }

}
