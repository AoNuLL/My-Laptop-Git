package com.ai.voicechanger.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ai.voicechanger.R

class SettingsFragment : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (savedInstanceState == null) {
            SettingsPreferenceFragment()
        } else {
            requireFragmentManager().findFragmentByTag("settings") as SettingsPreferenceFragment
        }.requireView()
    }
}

class SettingsPreferenceFragment : PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey)
        
        findPreference<Preference>("model_download_huggingface")?.setOnPreferenceClickListener {
            openUrl("https://huggingface.co/models?search=rvc")
            true
        }
        
        findPreference<Preference>("model_download_github")?.setOnPreferenceClickListener {
            openUrl("https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI/releases")
            true
        }
    }
    
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
