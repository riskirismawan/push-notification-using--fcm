package com.riski.firebasepushnotif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.riski.firebasepushnotif.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    companion object {
        const val TOPIC = "/topic/my_topic"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding.apply {
            getToken.setOnClickListener {
                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener {
                        token.setText(it)
                    }
                    .addOnFailureListener {
                        Log.e("MainActivity", "onCreate: $it", )
                    }
            }

            sendNotification.setOnClickListener {
                val title = title.text.toString()
                val message = message.text.toString()
                val token = toToken.text.toString()
                if (!title.isNullOrEmpty() && !message.isNullOrEmpty()) {
                    if (token.isNullOrEmpty()) {
                        PushNotification(
                            NotificationData(
                                title, message
                            ),
                            TOPIC
                        ).also {
                            sendNotification(it)
                        }
                    } else {
                        PushNotification(
                            NotificationData(
                                title, message
                            ),
                            token
                        ).also {
                            sendNotification(it)
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = ApiInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.e("MainActivity", "sendNotification success: ${Gson().toJson(response)}", )
            } else {
                Log.e("MainActivity", "sendNotification failled: ${response.body()}", )
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "sendNotification: Exception", e)
        }
    }
}