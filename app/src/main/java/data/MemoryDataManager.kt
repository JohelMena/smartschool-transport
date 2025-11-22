package com.johel.smartschool.data

class MemoryDataManager<T>(
    private val idSelector: (T) -> String
) : DataManager<T> {

    private val store = LinkedHashMap<String, T>()

    override fun create(item: T): T {
        store[idSelector(item)] = item
        return item
    }

    override fun getById(id: String): T? = store[id]

    override fun getAll(): List<T> = store.values.toList()

    override fun update(id: String, updater: (T) -> T): T? {
        val current = store[id] ?: return null
        val updated = updater(current)
        store[id] = updated
        return updated
    }

    override fun delete(id: String): Boolean = store.remove(id) != null
}
