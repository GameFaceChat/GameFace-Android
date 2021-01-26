/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.shop.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentEarnBinding

class EarnFragment : BaseFragment(), RewardedVideoAdListener {
    lateinit var bind : FragmentEarnBinding
    private lateinit var rewardedAd: RewardedAd

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentEarnBinding.inflate(inflater, container, false)
        rewardedAd = RewardedAd(requireActivity(), getString(R.string.rewarded_earn))
        bind.watchAd.isEnabled = false
        loadRewardedVideoAd()
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.watchAd.setOnClickListener {
            if (rewardedAd.isLoaded) {
                val activityContext = requireActivity()
                val adCallback = object: RewardedAdCallback() {
                    override fun onRewardedAdOpened() {}
                    override fun onRewardedAdClosed() {}

                    override fun onUserEarnedReward(p0: com.google.android.gms.ads.rewarded.RewardItem) {
                        Toast.makeText(requireContext(), "You have earned ${p0.amount} GamePoints!", Toast.LENGTH_LONG).show()
                    }

                    override fun onRewardedAdFailedToShow(adError: AdError) {
                        Toast.makeText(requireContext(), "Ad has failed to load! Please try again later!", Toast.LENGTH_LONG).show()
                    }
                }
                rewardedAd.show(activityContext, adCallback)
            }
            else {
                Toast.makeText(requireContext(), "Please wait a second!", Toast.LENGTH_LONG).show()
                Log.d("TAG", "The rewarded ad wasn't loaded yet.")
            }
        }

    }

    private fun loadRewardedVideoAd() {
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                bind.watchAd.isEnabled = true
            }
            override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                bind.watchAd.isEnabled = false
                Log.e("EarnFragment", "Ad Failed to Load ${adError.message}")
                Toast.makeText(requireContext(), adError.message, Toast.LENGTH_LONG).show()
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

    override fun onRewardedVideoAdLoaded() {}

    override fun onRewardedVideoAdOpened() {}

    override fun onRewardedVideoStarted() {}

    override fun onRewardedVideoAdClosed() {
        loadRewardedVideoAd()
    }

    override fun onRewarded(p0: RewardItem?) {

    }

    override fun onRewardedVideoAdLeftApplication() {}

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        Toast.makeText(requireContext(), "Ad Failed to load. Please try again later!", Toast.LENGTH_LONG).show()
    }

    override fun onRewardedVideoCompleted() {
    }

}