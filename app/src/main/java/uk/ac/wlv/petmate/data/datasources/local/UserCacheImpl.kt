package uk.ac.wlv.petmate.data.datasources.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import uk.ac.wlv.petmate.model.User

class UserCacheImpl(
    private val context: Context
) : UserCache {

    private val Context.dataStore by preferencesDataStore(name = "user_cache")

    private val ID = stringPreferencesKey("id")
    private val NAME = stringPreferencesKey("name")
    private val EMAIL = stringPreferencesKey("email")
    private val PHOTO = stringPreferencesKey("photo")

    override suspend fun saveUser(user: User) {
        context.dataStore.edit {
            it[ID] = user.id
            it[NAME] = user.name ?: ""
            it[EMAIL] = user.email ?: ""
            it[PHOTO] = user.photoUrl ?: ""
        }
    }

    override suspend fun getUser(): User? {
        val prefs = context.dataStore.data.first()
        val id = prefs[ID] ?: return null

        return User(
            id = id,
            name = prefs[NAME],
            email = prefs[EMAIL],
            photoUrl = prefs[PHOTO]
        )
    }

    override suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
