package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var client: TwitterClient
    lateinit var tvCharCount: TextView
    lateinit var colorSecondary: Integer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        val theme = this.theme
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorSecondary,typedValue, true)
        colorSecondary = Integer(typedValue.data)
        tvCharCount = findViewById(R.id.tvCharCount)
        etCompose = findViewById(R.id.etTweetCompose)
        etCompose.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    Log.i("garbage","having fun")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val count: Int? = s?.length
                tvCharCount.text = "${count}/240"
                if (count != null) {
                    if(count > 240){
                        tvCharCount.setTextColor(Color.RED)
                    }else{
                        tvCharCount.setTextColor(colorSecondary.toInt())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("garbage","having lots of fun")
            }

        })
        btnTweet = findViewById(R.id.btnTweet)
        client = TwitterApplication.getRestClient(this)

        btnTweet.setOnClickListener{
            val tweetContent = etCompose.text.toString()

            if(tweetContent.isEmpty()){
                Toast.makeText(this,"Empty tweets not allowed!", Toast.LENGTH_LONG).show()
            }else if( tweetContent.length > 240){
                Toast.makeText(this,"Tweet is too long", Toast.LENGTH_LONG).show()
            }else{
                client.publishTweet(tweetContent, object:JsonHttpResponseHandler(){
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e("CUSTOMA", "Failed to publish tweet",throwable)
                        val intent = Intent()
                        intent.putExtra("error","failed")
                        setResult(RESULT_CANCELED,intent)
                        finish()
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i("CUSTOMA", "Successfully published tweet?")
                        val tweet = Tweet.fromJson(json.jsonObject)
                        val intent = Intent()
                        intent.putExtra("tweet",tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                })
            }
        }
    }
}