package com.shashi.kotlincoroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val TAG = "debug_shashi"

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        //Dispatchers.Main -> For UI operations
        //Dispatchers.IO -> For Data operations(Networking/Database Handling)
        //Dispatchers.Default -> For Complex operations

        GlobalScope.launch(Dispatchers.Main) {
            Log.d(TAG, Thread.currentThread().name)

            delay(3000L)

            val networkCallAnswer1 = doNetworkCall()
            val networkCallAnswer2 = doNetworkCall2()

            Log.d(TAG, "Answer1: $networkCallAnswer1")
            Log.d(TAG, "Answer1: $networkCallAnswer2")
        }

        //Custom thread
        //GlobalScope.launch(newSingleThreadContext("CustomThreadName")) { }

        //If you want to get data from Network and then update UI in Main thread
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "IO thread: ${Thread.currentThread().name}")

            val answer = doNetworkCall()
            withContext(Dispatchers.Main) {
                //This would be executed in Main thread
                Log.d(TAG, "Main thread: ${Thread.currentThread().name}")
                textViewMain.text = answer
            }
        }

        //If you want to call a suspend fun without coroutine behaviour, use runBlocking
        runBlocking {
            //This blocks the Main thread
            delay(1000L)
        }
    }

    private suspend fun doNetworkCall(): String {
        Log.d(TAG, "Network call 1: ${Thread.currentThread().name}")

        delay(3000L)
        return "Network call 1"
    }

    private suspend fun doNetworkCall2(): String {
        Log.d(TAG, "Network call 2: ${Thread.currentThread().name}")

        delay(3000L)
        return "Network call 2"
    }

}