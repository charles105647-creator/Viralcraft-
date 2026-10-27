package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.PostScheduleEntity
import com.example.data.model.PostStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PostScheduleDao {
    @Query("SELECT * FROM post_schedules ORDER BY id DESC")
    fun getAllSchedules(): Flow<List<PostScheduleEntity>>

    @Query("SELECT * FROM post_schedules WHERE videoProjectId = :projectId")
    fun getSchedulesForProject(projectId: Long): Flow<List<PostScheduleEntity>>

    @Query("SELECT COUNT(*) FROM post_schedules WHERE status = :status")
    fun countByStatus(status: PostStatus): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: PostScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<PostScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: PostScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: PostScheduleEntity)
}
