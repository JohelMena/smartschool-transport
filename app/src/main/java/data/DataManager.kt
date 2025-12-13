package com.johel.smartschoolapp.data

interface DataManager<T> {
    fun add(item: T)
    fun getAll(): List<T>
    fun getById(id: String): T?
    fun removeById(id: String): Boolean
}
