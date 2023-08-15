package com.example.fetchyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchyapp.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    var itemCollectionsMap = HashMap<Int, ItemCollection>()
    var itemCollectionArray = arrayListOf<ItemCollection>()
    var mainHandler: Handler = Handler()
    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.dark_purple)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataFetcher().start()
        val recycler: RecyclerView = findViewById(R.id.recyclerView3)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ItemsViewAdapter(applicationContext, itemCollectionArray)

        binding.reloadData.setOnClickListener(View.OnClickListener() {
            itemCollectionArray.clear()
            dataFetcher().start()
            binding.recyclerView3.adapter?.notifyDataSetChanged()
            recycler.adapter = ItemsViewAdapter(applicationContext, itemCollectionArray)
        })
    }

    inner class dataFetcher: Thread() {

        var data: String = ""

        override fun run() {
            super.run()

            try {
                val url: URL = URL("https://fetch-hiring.s3.amazonaws.com/hiring.json")
                val httpUrlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                val inputStream: InputStream = httpUrlConnection.inputStream
                val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
                var line: String? = ""
                while (line != null){
                    line = bufferedReader.readLine()
                    data += line
                }

                if(data.isNotEmpty()){
                    var jsonItems: JSONArray =  JSONArray(data)
                    itemCollectionArray.clear()
                    itemCollectionsMap.clear()
                    for (i in 0 until jsonItems.length()) {
                        val jsonItem :JSONObject = jsonItems.getJSONObject(i)
                        val itemName: String = jsonItem.getString("name")

                        if(itemName != "" && itemName!="null"){

                            val itemID: Int = jsonItem.getInt("id")
                            val itemListID: Int = jsonItem.getInt("listId")
                            val item: Item = Item(id = itemID, name = itemName, listId = itemListID )

                            if(itemCollectionsMap.containsKey(itemListID)){
                                itemCollectionsMap[itemListID]?.addItem(item)
                            }else{
                                val newItemCollection: ItemCollection = ItemCollection(id=itemListID, items= arrayListOf(item))
                                itemCollectionsMap[itemListID] = newItemCollection
                            }
                        }
                    }

                    itemCollectionArray = ArrayList(itemCollectionsMap.values)
                }
            }catch (e: MalformedURLException){
                println(e.stackTrace)
            }catch (e: IOException){
                println(e.stackTrace)
            }

            synchronized(itemCollectionArray) {
                itemCollectionArray = ArrayList(itemCollectionsMap.values)
                for (i in 0..itemCollectionArray.size - 1) {
                    val sortedList = ArrayList<Item>(itemCollectionArray[i].getItems().sortedWith(compareBy({ it.id })))
                    itemCollectionArray[i].setItemList(sortedList)
                }

                mainHandler.post(Runnable {
                    run() {
                        binding.recyclerView3.adapter?.notifyDataSetChanged()
                        val recycler: RecyclerView = findViewById(R.id.recyclerView3)
                        recycler.adapter = ItemsViewAdapter(applicationContext, itemCollectionArray)
                    }
                })
            }
        }
    }
}