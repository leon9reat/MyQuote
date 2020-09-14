package com.medialink.myquote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getRandomQuote()

        btn_all_quotes.setOnClickListener {
            startActivity(Intent(this@MainActivity, ListQuotesActivity::class.java))
        }
    }

    private fun getRandomQuote() {
        progress_bar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://programming-quotes-api.herokuapp.com/quotes/random"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                // jika koneksi berhasil
                progress_bar.visibility = View.INVISIBLE
                val result = responseBody?.let { String(it) }
                Log.d(TAG, "onSuccess: $result")
                val responseObject = JSONObject(result)
                val quote = responseObject.getString("en")
                val author = responseObject.getString("author")

                tv_quote.text = quote
                tv_author.text = author
                try {
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                // jika koneksi gagal
                progress_bar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode: bad request"
                    403 -> "$statusCode: forbidden"
                    404 -> "$statusCode: not found"
                    else -> "$statusCode: ${error?.message}"
                }

                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }

        })
    }
}