package com.mapbox.navigation.navigator

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.os.Build
import android.util.Log
import com.mapbox.navigator.NavigatorSensorData
import com.mapbox.navigator.SensorType
import java.util.*

object SensorMapper {

    fun getSupportedSensorTypes(): Set<Int> {
        val supportedSensors = mutableSetOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_PRESSURE
        )
        if (Build.VERSION.SDK_INT >= 18) {
            supportedSensors.add(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)
            supportedSensors.add(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)
        }
        if (Build.VERSION.SDK_INT >= 26) {
            supportedSensors.add(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)
        }
        return supportedSensors
    }
}

fun SensorEvent.toNavigatorSensorData(): NavigatorSensorData? {
    val sensorType = sensor.toSensorType() ?: return null
    return NavigatorSensorData(
        sensorType,
        Date(),
        timestamp,
        values.toList())
}

fun Sensor.toSensorType(): SensorType? {
    return when (type) {
        Sensor.TYPE_ACCELEROMETER -> SensorType.ACCELEROMETER
        Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> SensorType.ACCELEROMETER
        Sensor.TYPE_MAGNETIC_FIELD -> SensorType.MAGNETOMETER
        Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> SensorType.MAGNETOMETER
        Sensor.TYPE_GYROSCOPE -> SensorType.GYROSCOPE
        Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> SensorType.GYROSCOPE
        Sensor.TYPE_GRAVITY -> SensorType.GRAVITY
        Sensor.TYPE_PRESSURE -> SensorType.PRESSURE
        else -> {
            Log.e("UnsupportedSensorEvent", "This type of sensor event is not supported: $name")
            null
        }
    }
}