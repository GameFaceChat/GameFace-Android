/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.repository

import com.magnitudestudios.GameFace.network.FirebaseHelper

object UserRepository {
    suspend fun loadUser(uid: String) {
        FirebaseHelper.getUserByUID(uid)
    }
}