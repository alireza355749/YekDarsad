package com.example.yekdarsad.data.sync

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun signIn(
        email: String,
        password: String
    ) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signUp(
        email: String,
        password: String
    ) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    fun currentUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }

    fun currentUserEmail(): String? {
        return supabase.auth.currentUserOrNull()?.email
    }

    fun isLoggedIn(): Boolean {
        return currentUserId() != null
    }
}