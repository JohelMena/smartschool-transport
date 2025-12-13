package com.johel.smartschoolapp.data

class MemoryDataManager<T>(private val idSelector: (T) -> String) {
    private val items = mutableListOf<T>()

    fun add(item: T) {
        items.add(item)
    }

    fun getAll(): List<T> {
        return items
    }

    fun getById(id: String): T? {
        return items.find { idSelector(it) == id }
    }

    fun removeById(id: String): Boolean {
        return items.removeIf { idSelector(it) == id }
    }
    fun clearAll() {
        items.clear()
    }
}
