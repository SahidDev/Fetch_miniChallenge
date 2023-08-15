package com.example.fetchyapp

class Item(id:Int,name:String,listId:Int) {
    val name: String = name
    val id: Int = id
    val listId: Int = listId

    fun getIdText(): String {
        return listId.toString()
    }

    fun getNameText(): String {
        return name
    }
    fun getListIdInt(): Int {
        return listId
    }
    fun getUniqueId(): Int{
        return id
    }
}