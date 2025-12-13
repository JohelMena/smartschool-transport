package com.johel.smartschoolapp.Db

import androidx.room.*

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY fullName")
    fun getAll(): List<StudentEntity>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getById(id: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(student: StudentEntity)

    @Update
    fun update(student: StudentEntity)

    @Delete
    fun delete(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :id")
    fun deleteById(id: String)

    @Query("DELETE FROM students")
    fun clearAll()
}
