package uk.ac.wlv.petmate.data.datasources.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import uk.ac.wlv.petmate.data.model.ApiUser

class UserCacheImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserCache {

    private val Context.dataStore by preferencesDataStore(name = "user_cache")

    private val ID         = stringPreferencesKey("id")
    private val NAME       = stringPreferencesKey("name")
    private val EMAIL      = stringPreferencesKey("email")
    private val PHOTO      = stringPreferencesKey("photo")
    private val PHONE      = stringPreferencesKey("phone")
    private val TOKEN      = stringPreferencesKey("token")
    private val CREATED_AT = stringPreferencesKey("created_at")
    private val IS_GOOGLE  = booleanPreferencesKey("is_google")

    // ── Save ApiUser ──────────────────────────────────────────────────────
    override suspend fun saveUser(user: ApiUser) {
        context.dataStore.edit {
            it[ID]         = user.id
            it[NAME]       = user.fullName ?: ""
            it[EMAIL]      = user.email ?: ""
            it[PHOTO]      = user.profilePhotoUrl ?: ""
            it[PHONE]      = user.phoneNumber ?: ""
            it[TOKEN]      = user.token
            it[CREATED_AT] = user.createdAt
            it[IS_GOOGLE]  = user.isGoogleUser
        }
    }

    // ── Get ApiUser ───────────────────────────────────────────────────────
    override suspend fun getUser(): ApiUser? {
        val prefs = context.dataStore.data.first()
        val id    = prefs[ID] ?: return null

        return ApiUser(
            id              = id,
            fullName        = prefs[NAME],
            email           = prefs[EMAIL],
            phoneNumber     = prefs[PHONE],
            profilePhotoUrl = prefs[PHOTO],
            isGoogleUser    = prefs[IS_GOOGLE] ?: false,
            token           = prefs[TOKEN] ?: "",
            createdAt       = prefs[CREATED_AT] ?: ""
        )
    }

    // ── Get Token ─────────────────────────────────────────────────────────
    override suspend fun getToken(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[TOKEN]?.takeIf { it.isNotEmpty() }
    }

    // ── Save Token separately ─────────────────────────────────────────────
    override suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[TOKEN] = token
        }
    }

    // ── Clear User only ───────────────────────────────────────────────────
    override suspend fun clearUser() {
        context.dataStore.edit {
            it.remove(ID)
            it.remove(NAME)
            it.remove(EMAIL)
            it.remove(PHOTO)
            it.remove(PHONE)
            it.remove(CREATED_AT)
            it.remove(IS_GOOGLE)
        }
    }

    // ── Clear Token only ──────────────────────────────────────────────────
    override suspend fun clearToken() {
        context.dataStore.edit {
            it.remove(TOKEN)
        }
    }

    // ── Clear everything ──────────────────────────────────────────────────
    override suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}