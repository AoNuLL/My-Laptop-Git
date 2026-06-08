package com.ai.voicechanger.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ai.voicechanger.data.local.AppDatabase
import com.ai.voicechanger.data.local.AudioFile
import com.ai.voicechanger.data.local.FilePathManager
import com.ai.voicechanger.data.model.VoiceModel
import com.ai.voicechanger.databinding.FragmentRecorderBinding
import com.ai.voicechanger.domain.processor.RVCInferenceModel
import com.ai.voicechanger.domain.processor.RVCAudioProcessor
import com.ai.voicechanger.domain.processor.RealTimeVoiceChanger
import com.ai.voicechanger.domain.recorder.AudioRecorder
import com.ai.voicechanger.domain.recorder.RecordingState
import com.ai.voicechanger.domain.tflite.TFLiteModelLoader
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecorderFragment : Fragment() {
    private var _binding: FragmentRecorderBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var database: AppDatabase
    private lateinit var voiceChanger: RealTimeVoiceChanger
    private lateinit var model: RVCInferenceModel
    private lateinit var audioProcessor: RVCAudioProcessor
    
    private var recordingStartTime: Long = 0
    private var currentFile: File? = null
    private var isRealTimeMode = false
    private var currentModel: VoiceModel? = null
    private var allModels: List<VoiceModel> = emptyList()
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.RECORD_AUDIO] == true) {
            startNormalRecording()
        } else {
            Toast.makeText(context, "需要录音权限", Toast.LENGTH_SHORT).show()
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
        
        audioRecorder = AudioRecorder()
        database = AppDatabase.get()
        
        val modelLoader = TFLiteModelLoader(requireContext())
        audioProcessor = RVCAudioProcessor()
        model = RVCInferenceModel(modelLoader)
        voiceChanger = RealTimeVoiceChanger(model, audioProcessor)
        
        setupUI()
        loadModels()
        observeRecordingState()
        observeRealTimeState()
    }
    
    private fun setupUI() {
        binding.btnRecord.setOnClickListener {
            if (isRealTimeMode) {
                toggleRealTimeRecording()
            } else {
                toggleNormalRecording()
            }
        }
        
        binding.switchVoiceChange.setOnCheckedChangeListener { _, isChecked ->
            isRealTimeMode = isChecked
            updateUIForMode()
        }
        
        binding.pitchSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                voiceChanger.setPitchChange(value)
                binding.tvPitchLabel.text = "音调：${value.toInt()}半音"
            }
        }
    }
    
    private fun loadModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.voiceModelDao().getAll().collectLatest { models ->
                    allModels = models
                    updateModelSpinner()
                }
            }
        }
    }
    
    private fun updateModelSpinner() {
        val modelNames = allModels.map { "${it.name} (${it.fileSize / 1024 / 1024}MB)" }.toTypedArray()
        val adapterName = if (modelNames.isEmpty()) arrayOf("请先导入模型") else modelNames
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            adapterName
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.modelSpinner.adapter = adapter
        
        if (allModels.isNotEmpty()) {
            currentModel = allModels[0]
            binding.modelSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                    currentModel = allModels.getOrNull(position)
                    loadSelectedModel()
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
        }
    }
    
    private fun loadSelectedModel() {
        currentModel?.let { modelInfo ->
            lifecycleScope.launch {
                try {
                    val success = model.loadModel(modelInfo.modelPath, modelInfo.useGPU)
                    Toast.makeText(context, if (success) "模型加载成功" else "模型加载失败", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "模型加载失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun updateUIForMode() {
        binding.btnRecord.text = if (isRealTimeMode) "开始变声" else "开始录音"
        binding.tvStatus.text = if (isRealTimeMode) "实时变声模式" else "普通录音模式"
        binding.pitchSlider.visibility = if (isRealTimeMode) View.VISIBLE else View.GONE
        binding.tvPitchLabel.visibility = if (isRealTimeMode) View.VISIBLE else View.GONE
    }
    
    private fun toggleRealTimeRecording() {
        if (currentModel == null) {
            Toast.makeText(context, "请先选择模型", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (!model.isModelLoaded()) {
            loadSelectedModel()
        }
        
        if (voiceChanger.state.value == RealTimeVoiceChanger.State.IDLE ||
            voiceChanger.state.value == RealTimeVoiceChanger.State.STOPPED) {
            startRealTimeRecording()
        } else {
            stopRealTimeRecording()
        }
    }
    
    private fun startRealTimeRecording() {
        lifecycleScope.launch {
            try {
                val result = voiceChanger.start()
                result.onSuccess {
                    binding.btnRecord.text = "停止变声"
                    binding.tvStatus.text = "正在变声..."
                    recordingStartTime = System.currentTimeMillis()
                }
                result.onFailure { error ->
                    Toast.makeText(context, "启动失败：${error.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "启动失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun stopRealTimeRecording() {
        voiceChanger.stop()
        binding.btnRecord.text = "开始变声"
        binding.tvStatus.text = "变声已停止"
    }
    
    private fun observeRealTimeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                voiceChanger.state.collectLatest { state ->
                    binding.tvStatus.text = when (state) {
                        RealTimeVoiceChanger.State.RECORDING -> "正在变声..."
                        RealTimeVoiceChanger.State.PROCESSING -> "处理中..."
                        RealTimeVoiceChanger.State.STOPPED -> "已停止"
                        RealTimeVoiceChanger.State.ERROR -> "发生错误"
                        else -> "未知状态"
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                voiceChanger.latencyMs.collectLatest { latency ->
                    binding.tvLatency.text = "延迟：${latency}ms"
                }
            }
        }
    }
    
    private fun toggleNormalRecording() {
        if (audioRecorder.recordingState.value is RecordingState.IDLE) {
            checkPermissionsAndRecord()
        } else {
            stopNormalRecording()
        }
    }
    
    private fun checkPermissionsAndRecord() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startNormalRecording()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
    
    private fun startNormalRecording() {
        currentFile = FilePathManager.createAudioFile("recording")
        val result = audioRecorder.startRecording(currentFile!!)
        
        if (result.isSuccess) {
            recordingStartTime = System.currentTimeMillis()
            binding.btnRecord.text = "停止录音"
            binding.tvStatus.text = "正在录音..."
        } else {
            Toast.makeText(context, "录音失败：${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun stopNormalRecording() {
        audioRecorder.stopRecording()
        binding.btnRecord.text = "开始录音"
        binding.tvStatus.text = "录音完成"
    }
    
    private fun observeRecordingState() {
        lifecycleScope.launch {
            audioRecorder.recordingState.collectLatest { state ->
                binding.btnRecord.text = "开始录音"
                binding.tvStatus.text = when (state) {
                    is RecordingState.IDLE -> {
                        currentFile?.let { file ->
                            if (file.exists() && !isRealTimeMode) {
                                saveToDatabase(file)
                            }
                        }
                        "准备录音"
                    }
                    is RecordingState.RECORDING -> "正在录音..."
                    is RecordingState.STOPPING -> "保存中..."
                    is RecordingState.ERROR -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        "录音错误"
                    }
                }
            }
        }
    }
    
    private fun saveToDatabase(file: File) {
        val duration = System.currentTimeMillis() - recordingStartTime
        val audioFile = AudioFile(
            name = "录音_${formatDate()}",
            filePath = file.absolutePath,
            duration = duration,
            createdAt = System.currentTimeMillis()
        )
        
        lifecycleScope.launch {
            try {
                database.audioFileDao().insert(audioFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun formatDate(): String {
        return SimpleDateFormat("MMdd_HHmm", Locale.getDefault()).format(Date(recordingStartTime))
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        audioRecorder.release()
        voiceChanger.stop()
        model.close()
    }
}
