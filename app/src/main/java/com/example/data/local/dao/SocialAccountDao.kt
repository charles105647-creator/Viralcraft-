package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.SocialAccountEntity
import com.example.data.model.SocialPlatform
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialAccountDao {
    @Query("SELECT * FROM social_accounts")
    fun getAllAccounts(): Flow<List<SocialAccountEntity>>

    @Query("SELECT * FROM social_accounts WHERE platform = :platform LIMIT 1")
    suspend fun getAccountByPlatform(platform: SocialPlatform): SocialAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAccount(account: SocialAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<SocialAccountEntity>)

    @Update
    suspend fun updateAccount(account: SocialAccountEntity)
}
