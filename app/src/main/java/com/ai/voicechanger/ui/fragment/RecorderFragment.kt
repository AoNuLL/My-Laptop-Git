package com.ai.voicechanger.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ai.voicechanger.R
import com.ai.voicechanger.data.local.AudioFile
import com.ai.voicechanger.data.local.AppDatabase
import com.ai.voicechanger.data.local.FilePathManager
import com.ai.voicechanger.databinding.FragmentRecorderBinding
import com.ai.voicechanger.domain.recorder.AudioRecorder
import com.ai.voicechanger.domain.recorder.RecordingState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RecorderFragment : Fragment() {
    private var _binding: FragmentRecorderBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var recorder: AudioRecorder
    private lateinit var database: AppDatabase
    
    private var recordingStartTime: Long = 0
    private var currentFile: java.io.File? = null
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.RECORD_AUDIO] == true) {
            startRecording()
        } else {
            Toast.makeText(requireContext(), R.string.permission_required, Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecorderBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recorder = AudioRecorder()
        database = AppDatabase.get()
        
        setupUI()
        observeRecordingState()
    }
    
    private fun setupUI() {
        binding.btnRecord.setOnClickListener {
            if (recorder.recordingState.value is RecordingState.IDLE) {
                checkPermissionsAndRecord()
            } else {
                stopRecording()
            }
        }
    }
    
    private fun checkPermissionsAndRecord() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startRecording()
            }
            else -> {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }
    
    private fun startRecording() {
        currentFile = FilePathManager.createAudioFile("recording")
        
        val result = recorder.startRecording(currentFile!!)
        
        if (result.isSuccess) {
            recordingStartTime = System.currentTimeMillis()
            binding.btnRecord.text = getString(R.string.stop_recording)
            binding.tvStatus.text = "正在录音..."
            startTimer()
        } else {
            Toast.makeText(
                requireContext(),
                "录音失败：${result.exceptionOrNull()?.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    private fun stopRecording() {
        recorder.stopRecording()
        binding.btnRecord.text = getString(R.string.start_recording)
        binding.tvStatus.text = "录音完成"
    }
    
    private fun startTimer() {
        binding.timer.start()
    }
    
    private fun observeRecordingState() {
        viewLifecycleOwner.lifecycleScope.launch {
            recorder.recordingState.collectLatest { state ->
                when (state) {
                    is RecordingState.IDLE -> {
                        binding.btnRecord.text = getString(R.string.start_recording)
                        binding.tvStatus.text = "准备录音"
                        binding.timer.stop()
                        
                        currentFile?.let { file ->
                            if (file.exists()) {
                                saveToDatabase(file)
                            }
                        }
                    }
                    is RecordingState.RECORDING -> {
                        binding.tvStatus.text = "正在录音..."
                    }
                    is RecordingState.STOPPING -> {
                        binding.tvStatus.text = "保存中..."
                    }
                    is RecordingState.ERROR -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        binding.tvStatus.text = "录音错误"
                    }
                }
            }
        }
    }
    
    private fun saveToDatabase(file: java.io.File) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val duration = (System.currentTimeMillis() - recordingStartTime)
                
                val audioFile = AudioFile(
                    name = "录音_${formatDate()}",
                    filePath = file.absolutePath,
                    duration = duration,
                    createdAt = System.currentTimeMillis()
                )
                
                database.audioFileDao().insert(audioFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun formatDate(): String {
        return SimpleDateFormat("MMdd_HHmm", Locale.getDefault())
            .format(Date(recordingStartTime))
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        recorder.release()
    }
}
