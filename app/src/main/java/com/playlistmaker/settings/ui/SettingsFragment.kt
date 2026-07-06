package com.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.loadSettings()
    }

    private fun setupListeners() {
        binding.buttonShareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.buttonSupport.setOnClickListener {
            viewModel.openSupport()
        }

        binding.buttonUserAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }

    private fun render(state: SettingsState) {
        binding.themeSwitcher.setOnCheckedChangeListener(null)

        if (binding.themeSwitcher.isChecked != state.isDarkThemeEnabled) {
            binding.themeSwitcher.isChecked = state.isDarkThemeEnabled
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.themeSwitcher.setOnCheckedChangeListener(null)
        _binding = null
    }
}