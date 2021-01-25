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

package com.magnitudestudios.GameFace.ui.shop

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.magnitudestudios.GameFace.pojo.Shop.ShowCaseItem
import com.magnitudestudios.GameFace.repository.ShopRepository
import kotlinx.coroutines.Dispatchers

/**
 * Shop view model
 *
 * @constructor Create empty Shop view model
 */
class ShopViewModel : ViewModel() {
    val charadesItems = liveData(Dispatchers.IO) { emit(ShopRepository.getCharadesItems()) }

    val wouldYouRatherItems = liveData(Dispatchers.IO) { emit(ShopRepository.getWouldYouRatherItems()) }

    val tOrDItems = liveData(Dispatchers.IO){ emit(ShopRepository.getTorDItems()) }

    val showcaseItems = liveData(Dispatchers.IO) { emit(ShopRepository.getShowcaseItems()) }

    val selectedShowcaseItem = MutableLiveData<ShowCaseItem?>()

    val selectedShowcase = Transformations.switchMap(selectedShowcaseItem) {
        return@switchMap liveData {
            if (it == null) emit(null)
            else emit(ShopRepository.getShopItem(it.game, it.pack))
        }
    }
}