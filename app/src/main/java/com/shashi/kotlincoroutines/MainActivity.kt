package com.shashi.kotlincoroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val TAG = "debug_shashi"

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        coroutineContext()

        updateUiInMainThread()

        runBlockingDemo()

        jobCancel()

        asyncAndAwait()
    }

    private fun coroutineContext() {
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

        //For Custom thread
        //GlobalScope.launch(newSingleThreadContext("CustomThreadName")) { }
    }

    private fun updateUiInMainThread() {
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
    }

    private fun runBlockingDemo() {
        //If you want to call a suspend fun without coroutine behaviour, use runBlocking
        runBlocking {
            //This blocks the Main thread
            delay(1000L)
        }
    }

    private fun jobCancel() {
        //Understanding Jobs, how to wait for job to complete and cancel a job
        val job: Job = GlobalScope.launch(Dispatchers.Default) {

            //Automatically cancels coroutine if it takes more than 3ms
            withTimeout(3000L) {
                //task
                if (isActive) {
                    //Executes only if the coroutine is not cancelled
                    //job.cancel()
                }
            }

        }

        runBlocking {
            job.join()      //Joins the coroutine, Main activity will wait for the coroutine
            job.cancel()    //Cancel the coroutine
        }
    }

    private fun asyncAndAwait() {
        //By default, 2 suspend functions are executed sequentially, but for
        //a network request, we want them to execut simultaneoursly

        GlobalScope.launch(Dispatchers.IO) {
            //measureTimeMillis() -> Meature the time taken to execute the code
            val time = measureTimeMillis {
                val networkCallAnswer1: Deferred<String> = async {
                    Log.d(TAG, "Answer1: ${Thread.currentThread().name}")
                    doNetworkCall()
                }
                val networkCallAnswer2 = async {
                    Log.d(TAG, "Answer2: ${Thread.currentThread().name}")
                    doNetworkCall2()
                }

                //await() blocks the current coroutine until networkCallAnswer1 is available
                Log.d(TAG, "Answer1: ${networkCallAnswer1.await()}")
                Log.d(TAG, "Answer1: ${networkCallAnswer2.await()}")
            }

            Log.d(TAG, "Time takes: $time ms")
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