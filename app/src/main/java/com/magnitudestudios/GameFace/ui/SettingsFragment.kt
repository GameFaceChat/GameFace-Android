/*
 * Copyright (c) 2021 -Srihari Vishnu - All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.magnitudestudios.GameFace.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.ToolbarBackBinding
import com.magnitudestudios.GameFace.ui.main.MainViewModel

/**
 * Settings fragment
 *
 * @constructor Create empty Settings fragment
 */
class SettingsFragment : PreferenceFragmentCompat() {
    lateinit var toolbar: ToolbarBackBinding
    lateinit var mainViewModel: MainViewModel
    val TITLE = "Settings"
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>("signOutPref")?.setOnPreferenceClickListener {
            mainViewModel.signOutUser()
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>("termsAndConditions")?.setOnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.termsAndConditionsLink))))
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>("privacyPolicy")?.setOnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacyPolicyLink))))
            return@setOnPreferenceClickListener true
        }
        findPreference<Preference>("aboutUs")?.setOnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.aboutUs))))
            return@setOnPreferenceClickListener true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        val layout = super.onCreateView(inflater, container, savedInstanceState)
        val viewWithToolbar = LinearLayout(context)
        viewWithToolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        viewWithToolbar.orientation = LinearLayout.VERTICAL
        toolbar = ToolbarBackBinding.inflate(inflater, container, false)
        toolbar.title.text = TITLE
        viewWithToolbar.addView(toolbar.root)
        viewWithToolbar.addView(layout)
        return viewWithToolbar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}