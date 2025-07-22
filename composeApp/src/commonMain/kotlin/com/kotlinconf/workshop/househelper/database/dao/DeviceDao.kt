package com.kotlinconf.workshop.househelper.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kotlinconf.workshop.househelper.database.entities.DeviceEntity
import com.kotlinconf.workshop.househelper.database.entities.DeviceType
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices")
    fun getAllDevices(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE areaId = :areaId")
    fun getDevicesForArea(areaId: String): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    fun getDeviceById(deviceId: String): Flow<DeviceEntity?>

    @Query("SELECT * FROM devices WHERE id = :deviceId AND type = :deviceType")
    fun getDeviceByIdAndType(deviceId: String, deviceType: DeviceType): Flow<DeviceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)

    @Update
    suspend fun updateDevice(device: DeviceEntity)

    @Delete
    suspend fun deleteDevice(device: DeviceEntity)

    @Query("DELETE FROM devices")
    suspend fun deleteAllDevices()

    @Query("DELETE FROM devices WHERE areaId = :areaId")
    suspend fun deleteDevicesForArea(areaId: String)

    @Query("UPDATE devices SET isOn = :isOn WHERE id = :deviceId")
    suspend fun updateDeviceToggleState(deviceId: String, isOn: Boolean)

    @Query("UPDATE devices SET brightness = :brightness WHERE id = :deviceId")
    suspend fun updateDeviceBrightness(deviceId: String, brightness: Int)

    @Query("UPDATE devices SET colorRed = :red, colorGreen = :green, colorBlue = :blue, colorAlpha = :alpha WHERE id = :deviceId")
    suspend fun updateDeviceColor(deviceId: String, red: Float, green: Float, blue: Float, alpha: Float)

    @Query("UPDATE devices SET name = :name WHERE id = :deviceId")
    suspend fun updateDeviceName(deviceId: String, name: String)

    @Query("SELECT * FROM devices WHERE isFavorite = 1")
    fun getFavoriteDevices(): Flow<List<DeviceEntity>>

    @Query("UPDATE devices SET isFavorite = :isFavorite WHERE id = :deviceId")
    suspend fun updateDeviceFavoriteStatus(deviceId: String, isFavorite: Boolean)
}
