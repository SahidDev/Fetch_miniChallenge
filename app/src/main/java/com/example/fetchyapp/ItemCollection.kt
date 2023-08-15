package com.example.fetchyapp

class ItemCollection(id:Int,items:ArrayList<Item>) {
    val id: Int = id
    private var itemList: ArrayList<Item> = items

    fun getIdText(): String {
        return id.toString()
    }
    fun getItems(): ArrayList<Item> {
        return itemList
    }
    fun addItem(item: Item) {
        itemList.add(item)
    }
    fun getSizeofItemList(): Int{
        return itemList.size
    }
    fun setItemList(items:ArrayList<Item>){
        itemList = items
    }
}