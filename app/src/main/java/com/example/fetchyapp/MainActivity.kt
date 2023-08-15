package com.example.fetchyapp

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
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


    var itemsArray: ArrayList<Item> = arrayListOf<Item>()
    var itemCollectionsMap = HashMap<Int, ItemCollection>()
    var itemCollectionArray = arrayListOf<ItemCollection>()
    var mainHandler: Handler = Handler()
    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataFetcher().start()
        val recycler: RecyclerView = findViewById(R.id.recyclerView3)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ItemsViewAdapter(applicationContext, itemCollectionArray)

        binding.reloadData.setOnClickListener(View.OnClickListener() {
            dataFetcher().start()
            binding.recyclerView3.adapter?.notifyDataSetChanged()
            recycler.adapter = ItemsViewAdapter(applicationContext, itemCollectionArray)
            //Log.d("Size", itemCollectionArray.size.toString())
        })
        //Log.d("Size", itemCollectionArray.size.toString())
    }

    inner class dataFetcher: Thread() {

        var data: String = ""

        override fun run() {
            super.run()

            try {
                var url: URL = URL("https://fetch-hiring.s3.amazonaws.com/hiring.json")
                var httpUrlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                var inputStream: InputStream = httpUrlConnection.inputStream
                var bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
                var line: String? = ""
                while (line != null){
                    line = bufferedReader.readLine()
                    data = data + line
                }

                if(data.isNotEmpty()){
                    //var jsonObject: JSONObject = JSONObject(data)
                    var jsonItems: JSONArray =  JSONArray(data)
                    itemsArray.clear()
                    for (i in 0..jsonItems!!.length() - 1) {
                        var jsonItem :JSONObject = jsonItems.getJSONObject(i)
                        var itemName: String = jsonItem.getString("name")

                        if(itemName != "" && itemName!="null"){

                            var itemID: Int = jsonItem.getInt("id")
                            var itemListID: Int = jsonItem.getInt("listId")
                            var item: Item = Item(id = itemID, name = itemName, listId = itemListID )
                            itemsArray.add(item)
                            /*
                            Log.d("Name: ", itemName)
                            Log.d("id: ", itemID.toString())
                            Log.d("id: ", itemListID.toString())*/

                            if(itemCollectionsMap.containsKey(itemListID)){
                                itemCollectionsMap[itemListID]?.addItem(item)
                            }else{
                                var newItemCollection: ItemCollection = ItemCollection(id=itemListID, items= arrayListOf(item))
                                itemCollectionsMap[itemListID] = newItemCollection
                            }
                        }
                    }

                    itemCollectionArray = ArrayList(itemCollectionsMap.values)
                    /*Log.d("SIZE", itemCollectionArray.size.toString())
                    for (i in 0..itemCollectionArray.size - 1){
                            Log.d("itemCollection: ", itemCollectionArray[i].getIdText())
                            Log.d("SIZE", itemCollectionArray[i].getSizeofItemList().toString())
                    }*/
                }
            }catch (e: MalformedURLException){
                println(e.stackTrace)
            }catch (e: IOException){
                println(e.stackTrace)
            }

            synchronized(itemCollectionArray) {
                itemCollectionArray = ArrayList(itemCollectionsMap.values)
                for (i in 0..itemCollectionArray.size - 1) {
                    var sortedList = ArrayList<Item>(itemCollectionArray[i].getItems().sortedWith(compareBy({ it.id })))
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