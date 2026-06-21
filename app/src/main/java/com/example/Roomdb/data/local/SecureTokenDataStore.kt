package com.example.Roomdb.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val Context.secureDataStore by preferencesDataStore(name = "secure_auth_store")

class SecureTokenDataStore(private val context: Context) {

    private val keyAlias = "testkonnect_auth_key"
    private val androidKeyStore = "AndroidKeyStore"

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
        (keyStore.getKey(keyAlias, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, androidKeyStore)
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        generator.init(spec)
        return generator.generateKey()
    }

    // Stored as base64(iv):base64(ciphertext)
    private fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val data = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
        return "$iv:$data"
    }

    private fun decrypt(payload: String): String {
        val (ivPart, dataPart) = payload.split(":", limit = 2)
        val iv = Base64.decode(ivPart, Base64.NO_WRAP)
        val cipherBytes = Base64.decode(dataPart, Base64.NO_WRAP)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(128, iv))
        return String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
    }

    suspend fun saveSession(accessToken: String, refreshToken: String, userId: String) {
        context.secureDataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = encrypt(accessToken)
            prefs[Keys.REFRESH_TOKEN] = encrypt(refreshToken)
            prefs[Keys.USER_ID] = encrypt(userId)
        }
    }

    val accessToken: Flow<String?> =
        context.secureDataStore.data.map { it[Keys.ACCESS_TOKEN]?.let(::decrypt) }

    val refreshToken: Flow<String?> =
        context.secureDataStore.data.map { it[Keys.REFRESH_TOKEN]?.let(::decrypt) }

    val userId: Flow<String?> =
        context.secureDataStore.data.map { it[Keys.USER_ID]?.let(::decrypt) }

    suspend fun getAccessTokenOnce(): String? = accessToken.firstOrNull()
    suspend fun getUserIdOnce(): String? = userId.firstOrNull()

    suspend fun clearSession() {
        context.secureDataStore.edit { it.clear() }
    }
}