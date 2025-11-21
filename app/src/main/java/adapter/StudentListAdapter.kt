package com.johel.smartschoolapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.johel.smartschoolapp.R
import com.johel.smartschoolapp.data.RouteStorage
import com.johel.smartschoolapp.data.StudentStorage
import com.johel.smartschoolapp.domain.Student

class StudentListAdapter(
    private val students: List<Student>
) : RecyclerView.Adapter<StudentListAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivItemPhoto)
        val tvName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvGrade: TextView = itemView.findViewById(R.id.tvItemGrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]

        holder.tvName.text = student.fullName

        val routeName = RouteStorage.getById(student.routeId)?.name ?: "No route"
        holder.tvGrade.text = "Grade: ${student.enrollmentCode} • Route: $routeName"

        val photo = StudentStorage.photos[student.id]
        if (photo != null) {
            holder.ivPhoto.setImageBitmap(photo)
        } else {
            holder.ivPhoto.setImageResource(R.mipmap.ic_launcher_round)
        }
    }

    override fun getItemCount(): Int = students.size
}
