/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.magnitudestudios.GameFace.repository.ShopRepository
import kotlinx.coroutines.Dispatchers

class ShopViewModel : ViewModel() {
    val charadesItems = liveData(Dispatchers.IO) { emit(ShopRepository.getCharadesItems()) }

    val wouldYouRatherItems = liveData(Dispatchers.IO) { emit(ShopRepository.getWouldYouRatherItems()) }

    val tOrDItems = liveData(Dispatchers.IO){ emit(ShopRepository.getTorDItems()) }


}