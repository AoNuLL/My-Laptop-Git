package com.ai.voicechanger.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.ai.voicechanger.R
import com.ai.voicechanger.data.local.AudioFile
import com.ai.voicechanger.databinding.ItemAudioFileBinding
import java.text.SimpleDateFormat
import java.util.*

class VoicePackAdapter(
    private var files: List<AudioFile>,
    private val onItemClick: (AudioFile) -> Unit
) : RecyclerView.Adapter<VoicePackAdapter.ViewHolder>() {
    
    inner class ViewHolder(private val binding: ItemAudioFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(audioFile: AudioFile) {
            binding.tvTitle.text = audioFile.name
            binding.tvDuration.text = formatDuration(audioFile.duration)
            binding.tvDate.text = formatDate(audioFile.createdAt)
            
            binding.root.setOnClickListener { onItemClick(audioFile) }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAudioFileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(files[position])
    }
    
    override fun getItemCount() = files.size
    
    private fun formatDuration(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes.toInt(), secs.toInt())
    }
    
    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
            .format(Date(timestamp))
    }
}
