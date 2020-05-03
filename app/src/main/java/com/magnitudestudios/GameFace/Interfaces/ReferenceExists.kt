package com.magnitudestudios.GameFace.Interfaces

import com.google.firebase.database.DataSnapshot

interface ReferenceExists {
    fun referenceExists(b: Boolean, data: DataSnapshot?)
}