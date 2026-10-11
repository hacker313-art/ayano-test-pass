package com.example.model

// HINT: Replace this UID with your own Firebase Admin user UID.
// Find it in Firebase Console → Authentication → Users tab → copy the UID column.
// Keep it secret — do not share this file publicly with your real UID inside.
const val ADMIN_FIREBASE_UID = "BO98jRolQ0eueqnwZJ2D8PxSiae2"

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photo: String = "",
    val userClass: String = "Dropper", // "11", "12", "Dropper"
    // BUG FIX #3: The original code used a conditional default expression:
    //   val role: String = if (userId == ADMIN_FIREBASE_UID) "admin" else "student"
    // This works fine when constructing manually, BUT Firestore uses a no-arg
    // constructor + reflection when deserializing documents. That means userId
    // is empty string when role's default is evaluated → role always becomes
    // "student" even for admin, and isAdmin always returns false after reading
    // from Firestore.
    // FIX: Store role as a plain field with a safe default, and compute
    // isAdmin from BOTH the stored role AND the userId check. This way it
    // works whether the object was built in code or loaded from Firestore.
    val role: String = "student",
    val createdAt: Long = System.currentTimeMillis()
) {
    // isAdmin is true if EITHER the stored role says "admin" OR the userId
    // matches the hardcoded admin UID. Belt-and-suspenders approach.
    val isAdmin: Boolean
        get() = role == "admin" || userId == ADMIN_FIREBASE_UID
}
