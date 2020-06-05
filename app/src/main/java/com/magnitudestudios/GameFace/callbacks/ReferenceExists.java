package com.magnitudestudios.GameFace.callbacks;

import com.google.firebase.database.DataSnapshot;

public interface ReferenceExists {
    void referenceExists(boolean b, DataSnapshot data);
}

