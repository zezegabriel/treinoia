package com.treinoia.app.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Guarda a chave da API da Anthropic (Claude) em SharedPreferences local ao dispositivo.
 *
 * Aviso de segurança: isso NÃO é criptografado. Para uso pessoal está ok, mas se o app
 * for distribuído para outras pessoas, migre para EncryptedSharedPreferences
 * (androidx.security:security-crypto) ou, melhor ainda, para um backend que guarda a
 * chave do lado do servidor.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("treinoia_prefs", Context.MODE_PRIVATE)

    fun getApiKey(): String? = prefs.getString(KEY_API, null)

    fun saveApiKey(key: String) {
        prefs.edit().putString(KEY_API, key).apply()
    }

    fun hasApiKey(): Boolean = !getApiKey().isNullOrBlank()

    companion object {
        private const val KEY_API = "anthropic_api_key"
    }
}
