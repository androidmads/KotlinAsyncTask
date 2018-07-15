package com.androidmads.kotlinasynctask

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val tag: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallBtn.setOnClickListener {
            AsyncTaskExample(this).execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    class AsyncTaskExample(private var activity: MainActivity?) : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            activity?.MyprogressBar?.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: String?): String {

            var result = ""
            try {
                val url = URL("http://demosmushtaq.16mb.com/api/post_sample.php?version_index=14")
                val httpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.readTimeout = 8000
                httpURLConnection.connectTimeout = 8000
                httpURLConnection.doOutput = true
                httpURLConnection.connect()

                val responseCode: Int = httpURLConnection.responseCode
                Log.d(activity?.tag, "responseCode - " + responseCode)

                if (responseCode == 200) {
                    val inStream: InputStream = httpURLConnection.inputStream
                    val isReader = InputStreamReader(inStream)
                    val bReader = BufferedReader(isReader)
                    var tempStr: String?

                    try {

                        while (true) {
                            tempStr = bReader.readLine()
                            if (tempStr == null) {
                                break
                            }
                            result += tempStr
                        }
                    } catch (Ex: Exception) {
                        Log.e(activity?.tag, "Error in convertToString " + Ex.printStackTrace())
                    }
                }
            } catch (ex: Exception) {
                Log.d("", "Error in doInBackground " + ex.message)
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            activity?.MyprogressBar?.visibility = View.INVISIBLE
            if (result == "") {
                activity?.my_text?.text = activity?.getString(R.string.network_error)
            } else {
                var parsedResult = ""
                var jsonObject: JSONObject? = JSONObject(result)
                jsonObject = jsonObject?.getJSONObject("data")
                parsedResult += "Code Name : " + (jsonObject?.get("code_name")) + "\n"
                parsedResult += "Version Number : " + (jsonObject?.get("version_number")) + "\n"
                parsedResult += "API Level : " + (jsonObject?.get("api_level"))
                activity?.my_text?.text = parsedResult
            }
        }
    }
}
