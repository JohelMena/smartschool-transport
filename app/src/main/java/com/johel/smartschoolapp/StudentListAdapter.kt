package com.johel.smartschoolapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.johel.smartschoolapp.api.StudentDto

class StudentListAdapter(
    private val items: List<StudentDto>,
    private val onClick: (StudentDto) -> Unit
) : RecyclerView.Adapter<StudentListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto: ImageView = view.findViewById(R.id.ivItemPhoto)
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvGrade: TextView = view.findViewById(R.id.tvItemGrade)
        val tvGuardian: TextView = view.findViewById(R.id.tvItemGuardian)
        val tvId: TextView = view.findViewById(R.id.tvItemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = items[position]

        holder.tvName.text = student.fullName
        holder.tvGrade.text = "Grade: ${student.grade}"

        val guardianLabel = student.guardianId ?: "Not assigned"
        holder.tvGuardian.text = "Guardian: $guardianLabel"

        val idLabel = student.id ?: "(no id)"
        holder.tvId.text = "ID: $idLabel"


        holder.ivPhoto.setImageResource(R.mipmap.ic_launcher_round)

        holder.itemView.setOnClickListener {
            onClick(student)
        }
    }
}
