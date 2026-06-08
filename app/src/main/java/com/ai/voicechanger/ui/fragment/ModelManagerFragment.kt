package com.ai.voicechanger.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ai.voicechanger.R
import com.ai.voicechanger.data.local.AppDatabase
import com.ai.voicechanger.data.model.VoiceModel
import com.ai.voicechanger.data.repository.VoiceModelRepository
import com.ai.voicechanger.databinding.FragmentModelManagerBinding
import com.ai.voicechanger.ui.adapter.VoiceModelAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class ModelManagerFragment : Fragment() {
    private var _binding: FragmentModelManagerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: VoiceModelAdapter
    private lateinit var repository: VoiceModelRepository
    private lateinit var database: AppDatabase
    
    private val pickModelLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { importModel(it, null) }
    }
    
    private val pickIndexLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { lastModelUri = it }
    }
    
    private var lastModelUri: android.net.Uri? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModelManagerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        database = AppDatabase.get()
        repository = VoiceModelRepository(requireContext())
        
        setupUI()
        loadModels()
    }
    
    private fun setupUI() {
        binding.btnImport.setOnClickListener {
            showImportDialog()
        }
        
        adapter = VoiceModelAdapter(
            emptyList(),
            onModelClick = { },
            onDeleteClick = { model -> deleteModel(model) }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }
    
    private fun loadModels() {
        viewLifecycleOwner.lifecycleScope.launch {
            database.voiceModelDao().getAll().collectLatest { models ->
                adapter.updateList(models)
                
                binding.emptyView.isVisible = models.isEmpty()
                binding.recyclerView.isVisible = models.isNotEmpty()
            }
        }
    }
    
    private fun showImportDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("导入 RVC 模型")
            .setMessage("请选择要导入的模型文件 (.pth)\n\n可选：之后可选择 .index 文件")
            .setPositiveButton("选择模型文件") { _, _ ->
                pickModelLauncher.launch("audio/*")
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun importModel(modelUri: android.net.Uri, indexUri: android.net.Uri?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = repository.importModel(modelUri, indexUri)
                
                result.onSuccess { model ->
                    Toast.makeText(
                        requireContext(),
                        "模型 ${model.name} 导入成功",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                result.onFailure { error ->
                    Toast.makeText(
                        requireContext(),
                        "导入失败：${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "导入失败：${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun deleteModel(model: VoiceModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("确定要删除模型 \"${model.name}\" 吗？\n\n这将同时删除模型文件，无法恢复！")
            .setPositiveButton("删除") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val result = repository.deleteModel(model)
                        
                        result.onSuccess {
                            Toast.makeText(
                                requireContext(),
                                "模型已删除",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        
                        result.onFailure { error ->
                            Toast.makeText(
                                requireContext(),
                                "删除失败：${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "删除失败：${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
