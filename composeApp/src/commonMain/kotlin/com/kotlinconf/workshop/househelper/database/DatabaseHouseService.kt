package com.kotlinconf.workshop.househelper.database

import androidx.compose.ui.graphics.Color
import com.kotlinconf.workshop.househelper.Area
import com.kotlinconf.workshop.househelper.CameraDevice
import com.kotlinconf.workshop.househelper.Device
import com.kotlinconf.workshop.househelper.LightDevice
import com.kotlinconf.workshop.househelper.SwitchDevice
import com.kotlinconf.workshop.househelper.Toggleable
import com.kotlinconf.workshop.househelper.data.HouseService
import com.kotlinconf.workshop.househelper.database.entities.DeviceEntity
import com.kotlinconf.workshop.househelper.utils.imageUrls
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class DatabaseHouseService(private val database: AppDatabase) : HouseService {

    override fun getAreas(): Flow<List<Area>> {
        return database.roomDao().getAllAreas().map { areaEntities ->
            areaEntities.map { it.toArea() }
        }
    }

    override fun getDevicesForArea(areaId: String): Flow<List<Device>> {
        return database.deviceDao().getDevicesForArea(areaId).map { deviceEntities ->
            deviceEntities.map { it.toDevice() }
        }
    }

    override fun getDevice(deviceId: String): Flow<Device?> {
        return database.deviceDao().getDeviceById(deviceId).map { deviceEntity ->
            deviceEntity?.toDevice()
        }
    }

    override fun getCamera(deviceId: String): Flow<CameraDevice?> {
        return getDevice(deviceId).map { it as? CameraDevice }
    }

    override fun getCameraFootage(deviceId: String): Flow<String> {
        return flow {
            while (true) {
                val images = imageUrls.shuffled()
                for (image in images) {
                    emit(image)
                    delay(5000)
                }
            }
        }
    }

    override suspend fun toggle(deviceId: String): Boolean {
        val device: Device? = getDevice(deviceId).first()

        if (device !is Toggleable) {
            return false
        }

        val deviceDao = database.deviceDao()
        when (device) {
            is SwitchDevice -> deviceDao.updateDeviceToggleState(deviceId, !device.isOn)
            is CameraDevice -> deviceDao.updateDeviceToggleState(deviceId, !device.isOn)
            is LightDevice -> {
                val newIsOn = !device.isOn
                if (newIsOn && device.brightness == 0) {
                    deviceDao.updateDeviceToggleState(deviceId, true)
                    deviceDao.updateDeviceBrightness(deviceId, 100)
                } else {
                    deviceDao.updateDeviceToggleState(deviceId, newIsOn)
                }
            }
        }
        return true
    }

    override suspend fun setBrightness(deviceId: String, brightness: Int) {
        database.deviceDao().updateDeviceBrightness(deviceId, brightness)
    }

    override suspend fun setColor(deviceId: String, color: Color) {
        database.deviceDao().updateDeviceColor(
            deviceId,
            color.red,
            color.green,
            color.blue,
            color.alpha
        )
    }

    override suspend fun rename(deviceId: String, name: String) {
        database.deviceDao().updateDeviceName(deviceId, name)
    }

    override suspend fun toggleFavorite(deviceId: String): Boolean {
        val device = getDevice(deviceId).first() ?: return false
        val newFavoriteStatus = !device.isFavorite
        database.deviceDao().updateDeviceFavoriteStatus(deviceId, newFavoriteStatus)
        return newFavoriteStatus
    }

    override fun getFavoriteDevices(): Flow<List<Device>> {
        val favoriteDevices: Flow<List<DeviceEntity>> = database.deviceDao().getFavoriteDevices()
        return favoriteDevices.map { deviceEntities ->
            deviceEntities.map { it.toDevice() }
        }
    }
}
