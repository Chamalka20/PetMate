package uk.ac.wlv.petmate.data.datasources.local

import uk.ac.wlv.petmate.model.User

interface UserCache {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User?
    suspend fun clear()
}
