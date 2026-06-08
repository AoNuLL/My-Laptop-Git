package com.ai.voicechanger.ui.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ai.voicechanger.service.FloatWindowService

class SettingsFragment : PreferenceFragmentCompat() {
    
    private val overlayLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(requireContext())) {
                Toast.makeText(requireContext(), "悬浮窗权限已授予", Toast.LENGTH_SHORT).show()
                startFloatWindow()
            } else {
                Toast.makeText(requireContext(), "未授予悬浮窗权限", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(com.ai.voicechanger.R.xml.preferences_settings, rootKey)
        
        findPreference<Preference>("model_download_huggingface")?.setOnPreferenceClickListener {
            openUrl("https://huggingface.co/models?search=rvc")
            true
        }
        
        findPreference<Preference>("model_download_github")?.setOnPreferenceClickListener {
            openUrl("https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI/releases")
            true
        }
        
        findPreference<Preference>("float_window")?.setOnPreferenceClickListener {
            checkOverlayPermission()
            true
        }
    }
    
    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(requireContext())) {
                startFloatWindow()
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${requireContext().packageName}")
                )
                overlayLauncher.launch(intent)
            }
        } else {
            startFloatWindow()
        }
    }
    
    private fun startFloatWindow() {
        if (!isServiceRunning(FloatWindowService::class.java)) {
            val intent = Intent(requireContext(), FloatWindowService::class.java)
            intent.action = "TOGGLE"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(intent)
            } else {
                requireContext().startService(intent)
            }
            Toast.makeText(requireContext(), "悬浮窗已开启", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(requireContext(), FloatWindowService::class.java)
            intent.action = "HIDE"
            requireContext().startService(intent)
            Toast.makeText(requireContext(), "悬浮窗已关闭", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }
    
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
