package com.ai.voicechanger.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ai.voicechanger.data.local.AppDatabase
import com.ai.voicechanger.data.local.AudioFile
import com.ai.voicechanger.databinding.FragmentPlayerBinding
import com.ai.voicechanger.domain.player.AudioPlayer
import com.ai.voicechanger.ui.adapter.VoicePackAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var database: AppDatabase
    private lateinit var adapter: VoicePackAdapter
    private lateinit var audioPlayer: AudioPlayer
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        database = AppDatabase.get()
        audioPlayer = AudioPlayer()
        
        adapter = VoicePackAdapter(
            emptyList(),
            onItemClick = { audioFile -> playAudio(audioFile) },
            onDeleteClick = { audioFile -> showDeleteConfirmation(audioFile) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.audioFileDao().getAll().collectLatest { files ->
                    adapter.updateFiles(files)
                    binding.emptyView.visibility = if (files.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerView.visibility = if (files.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }
    
    private fun playAudio(audioFile: AudioFile) {
        val file = File(audioFile.filePath)
        if (file.exists()) {
            audioPlayer.playFile(file)
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("错误")
                .setMessage("文件不存在")
                .setPositiveButton("确定", null)
                .show()
        }
    }
    
    private fun showDeleteConfirmation(audioFile: AudioFile) {
        AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("确定要删除 ${audioFile.name} 吗？")
            .setPositiveButton("删除") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val file = File(audioFile.filePath)
                        if (file.exists() && file.delete()) {
                            database.audioFileDao().delete(audioFile)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        audioPlayer.release()
    }
}
