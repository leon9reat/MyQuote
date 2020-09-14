package com.medialink.myquote

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_list_quotes.*
import org.json.JSONArray

class ListQuotesActivity : AppCompatActivity() {

    companion object {
        private val TAG = ListQuotesActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_quotes)

        supportActionBar?.title = "List of quotes"
        getListQuotes()
    }

    private fun getListQuotes() {
        progress_bar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://programming-quotes-api.herokuapp.com/quotes/page/1"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                // jika koneksi berhasil
                progress_bar.visibility = View.INVISIBLE

                // parsing json
                val listQuote = ArrayList<String>()
                val result = responseBody?.let { String(it) }
                Log.d(TAG, "onSuccess: $result")

                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject =jsonArray.getJSONObject(i)
                        val quote = jsonObject.getString("en")
                        val author = jsonObject.getString("author")
                        listQuote.add("\n$quote\n- $author\n")
                    }

                    val adapter = ArrayAdapter(this@ListQuotesActivity, android.R.layout.simple_list_item_1, listQuote)

                    // listQuoted disini adalah nama list pada layout. cek apakah nama sudah sama
                    list_quotes.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this@ListQuotesActivity, e.message, Toast.LENGTH_LONG).show()
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

                val errorMsg = when (statusCode) {
                    401 -> "$statusCode: bad request"
                    403 -> "$statusCode: forbidden"
                    404 -> "$statusCode: not found"
                    else -> "$statusCode: ${error?.message}"
                }

                Toast.makeText(this@ListQuotesActivity, errorMsg, Toast.LENGTH_LONG).show()
            }

        })
    }
}