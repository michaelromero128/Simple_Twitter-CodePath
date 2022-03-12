package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
}