package com.ai.voicechanger.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ai.voicechanger.data.local.AppDatabase
import com.ai.voicechanger.data.local.AudioFile
import com.ai.voicechanger.databinding.FragmentPlayerBinding
import com.ai.voicechanger.ui.adapter.VoicePackAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var database: AppDatabase
    private lateinit var adapter: VoicePackAdapter
    
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
        setupRecyclerView()
        loadAudioFiles()
    }
    
    private fun setupRecyclerView() {
        adapter = VoicePackAdapter(emptyList()) { audioFile: AudioFile ->
            // Handle click
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }
    
    private fun loadAudioFiles() {
        viewLifecycleOwner.lifecycleScope.launch {
            database.audioFileDao().getAll().collectLatest { files ->
                adapter = VoicePackAdapter(files) { }
                binding.recyclerView.adapter = adapter
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
