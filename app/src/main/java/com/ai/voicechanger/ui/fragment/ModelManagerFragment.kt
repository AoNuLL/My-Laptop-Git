package com.ai.voicechanger.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ai.voicechanger.data.local.AppDatabase
import com.ai.voicechanger.data.model.VoiceModel
import com.ai.voicechanger.data.repository.VoiceModelRepository
import com.ai.voicechanger.databinding.FragmentModelManagerBinding
import com.ai.voicechanger.ui.adapter.VoiceModelAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ModelManagerFragment : Fragment() {
    private var _binding: FragmentModelManagerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: VoiceModelAdapter
    private lateinit var repository: VoiceModelRepository
    private lateinit var database: AppDatabase
    
    private var pendingPthUri: android.net.Uri? = null
    private var pendingIndexUri: android.net.Uri? = null
    
    private val pickModelLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { 
            pendingPthUri = it
            pickIndexLauncher.launch("audio/*")
        }
    }
    
    private val pickIndexLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { pendingIndexUri = it }
        if (pendingPthUri != null) {
            importModel(pendingPthUri!!, pendingIndexUri)
        }
    }
    
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
        
        adapter = VoiceModelAdapter(
            emptyList(),
            onModelClick = { model -> },
            onDeleteClick = { model -> deleteModel(model) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        
        binding.btnImport.setOnClickListener {
            pickModelLauncher.launch("audio/*")
        }
        
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.voiceModelDao().getAll().collectLatest { models ->
                    adapter.updateList(models)
                    binding.emptyView.visibility = if (models.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerView.visibility = if (models.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }
    
    private fun importModel(pthUri: android.net.Uri, indexUri: android.net.Uri?) {
        lifecycleScope.launch {
            try {
                val result = repository.importModel(pthUri, indexUri)
                Toast.makeText(context, if (result.isSuccess) "导入成功" else "导入失败：${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "导入失败：" + e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun deleteModel(model: VoiceModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("确定要删除 ${model.name} 吗？")
            .setPositiveButton("删除") { _, _ ->
                lifecycleScope.launch {
                    try {
                        repository.deleteModel(model)
                    } catch (e: Exception) {
                        Toast.makeText(context, "删除失败：" + e.message, Toast.LENGTH_SHORT).show()
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
