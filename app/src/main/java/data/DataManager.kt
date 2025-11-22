package com.johel.smartschool.data

interface DataManager<T> {
    fun create(item: T): T
    fun getById(id: String): T?
    fun getAll(): List<T>
    fun update(id: String, updater: (T) -> T): T?
    fun delete(id: String): Boolean
}
