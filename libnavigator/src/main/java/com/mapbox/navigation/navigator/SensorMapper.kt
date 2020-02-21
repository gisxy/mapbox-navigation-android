package com.mapbox.navigation.navigator

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.mapbox.navigator.Axes3D
import com.mapbox.navigator.NavigatorSensorData
import com.mapbox.navigator.SensorData
import com.mapbox.navigator.SensorType
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

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

internal fun SensorEvent.toNavigatorSensorData(): NavigatorSensorData? {
    val sensorType = this.toSensorType() ?: return null
    return NavigatorSensorData(
        sensorType,
        this.toTime(),
        this.timestamp,
        this.toList())
}

private fun SensorEvent.toSensorType(): SensorType? {
    return when (this.sensor.type) {
        Sensor.TYPE_ACCELEROMETER -> SensorType.ACCELEROMETER
        Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> SensorType.ACCELEROMETER
        Sensor.TYPE_MAGNETIC_FIELD -> SensorType.MAGNETOMETER
        Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> SensorType.MAGNETOMETER
        Sensor.TYPE_GYROSCOPE -> SensorType.GYROSCOPE
        Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> SensorType.GYROSCOPE
        Sensor.TYPE_GRAVITY -> SensorType.GRAVITY
        Sensor.TYPE_PRESSURE -> SensorType.PRESSURE
        else -> {
            Log.e("UnsupportedSensorEvent", "This type of sensor event is not supported: ${this.sensor.name}")
            null
        }
    }
}

private fun SensorEvent.toList(): MutableList<Float> {
    val floatList = mutableListOf<Float>()
    for (value in this.values) {
        floatList.add(value)
    }
    return floatList
}

private fun SensorEvent.toTime(): Date {
    return if (Build.VERSION.SDK_INT < 26) {
        val instantMillis = TimeUnit.NANOSECONDS.toMillis(this.timestamp)
        Date(instantMillis)
    } else {
        val instantTime = this.timestamp
        val instantFullSeconds = instantTime / TimeUnit.SECONDS.toNanos(1)
        val instantNanos = instantTime - instantFullSeconds
        val instantSecs = TimeUnit.NANOSECONDS.toSeconds(instantFullSeconds)
        val instant: Instant = Instant.ofEpochSecond(instantSecs, instantNanos)
        Date.from(instant)
    }
}
