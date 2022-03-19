package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {
    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetsAdapter
    val tweets = ArrayList<Tweet>()
    lateinit var swipeContainer: SwipeRefreshLayout
    val COMPOSE_ACTIVITY: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        swipeContainer = findViewById(R.id.swipeContainer)
        client = TwitterApplication.getRestClient(this)
        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter
        swipeContainer.setOnRefreshListener {
            Log.i("CUSTOMA","refresh intiated")
            fetchTimelineAsync(0)
        }
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
        populateHomeTimeLine()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId ==R.id.compose){
            //Toast.makeText(this,"Ready to compose tweet!", Toast.LENGTH_LONG)
            val intent = Intent(this,ComposeActivity::class.java)
            startActivityForResult(intent, COMPOSE_ACTIVITY)
        }
        return super.onOptionsItemSelected(item)
    }

    fun fetchTimelineAsync(page:Int){
        populateHomeTimeLine()
    }
    fun populateHomeTimeLine(){
        client.populateHomeTimeline(object: JsonHttpResponseHandler(){
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i("CUSTOMA","OnFailure")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i("CUSTOMA","onSuccess!")
                try {
                    adapter.clear()
                    val jsonArray = json.jsonArray
                    val listOfNewtweets = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(listOfNewtweets)
                    adapter.notifyDataSetChanged()
                    swipeContainer.setRefreshing(false)
                }catch(e: JSONException){
                    Log.e("CUSTOMA", "JSON exception $e")
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK && requestCode == COMPOSE_ACTIVITY){
            Log.i("CUSTOMA","refresh triggered")
            val tweet = data?.getParcelableExtra("tweet") as Tweet
            Log.i("CUSTOMA","here is the body of the tweet:${tweet.body}")
            adapter.tweets.add(0,tweet)
            adapter.notifyItemInserted(0)
            rvTweets.smoothScrollToPosition(0)
            Log.i("CUSTOMA","element at 0:${tweets.get(0).body}")
        }else {
            Log.i("CUSTOMA","did not trip refresh")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}