package com.ai.voicechanger.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ai.voicechanger.data.model.VoiceModel
import com.ai.voicechanger.databinding.ItemVoiceModelBinding
import java.text.SimpleDateFormat
import java.util.*

class VoiceModelAdapter(
    private var models: List<VoiceModel>,
    private val onModelClick: (VoiceModel) -> Unit,
    private val onDeleteClick: (VoiceModel) -> Unit
) : RecyclerView.Adapter<VoiceModelAdapter.ViewHolder>() {
    
    inner class ViewHolder(private val binding: ItemVoiceModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(model: VoiceModel) {
            binding.tvName.text = model.name
            binding.tvSize.text = formatSize(model.fileSize)
            binding.tvDate.text = formatDate(model.createdAt)
            binding.tvDesc.text = model.description
            
            binding.root.setOnClickListener { onModelClick(model) }
            binding.btnDelete.setOnClickListener { onDeleteClick(model) }
        }
        
        private fun formatSize(bytes: Long): String {
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            
            return when {
                mb >= 1 -> String.format("%.2f MB", mb)
                kb >= 1 -> String.format("%.2f KB", kb)
                else -> "$bytes B"
            }
        }
        
        private fun formatDate(timestamp: Long): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date(timestamp))
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVoiceModelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(models[position])
    }
    
    override fun getItemCount() = models.size
    
    fun updateList(newModels: List<VoiceModel>) {
        models = newModels
        notifyDataSetChanged()
    }
}
