package com.ai.voicechanger.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ai.voicechanger.service.FloatWindowService

class SettingsFragment : PreferenceFragmentCompat() {
    
    private val overlayLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(com.ai.voicechanger.R.xml.preferences_settings, rootKey)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
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
        activity?.let { activity ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(activity)) {
                    startFloatWindow()
                } else {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${activity.packageName}")
                    )
                    overlayLauncher.launch(intent)
                }
            } else {
                startFloatWindow()
            }
        }
    }
    
    private fun startFloatWindow() {
        activity?.let { activity ->
            if (!isServiceRunning(activity, FloatWindowService::class.java)) {
                val intent = Intent(activity, FloatWindowService::class.java)
                intent.action = "TOGGLE"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activity.startForegroundService(intent)
                } else {
                    activity.startService(intent)
                }
                Toast.makeText(activity, "悬浮窗已开启", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, FloatWindowService::class.java)
                intent.action = "HIDE"
                activity.startService(intent)
                Toast.makeText(activity, "悬浮窗已关闭", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }
    
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
