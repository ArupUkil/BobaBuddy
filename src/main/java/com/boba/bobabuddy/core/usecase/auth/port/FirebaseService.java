package com.boba.bobabuddy.core.usecase.auth.port;

import com.google.firebase.auth.FirebaseToken;

public interface FirebaseService {

	FirebaseToken parseToken(String idToken);

}