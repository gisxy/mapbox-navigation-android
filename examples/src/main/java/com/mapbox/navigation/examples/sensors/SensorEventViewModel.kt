package com.mapbox.navigation.examples.sensors

import android.app.Application
import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel

class SensorEventViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val navigationSensorManager = NavigationSensorManager(sensorManager)

    var eventEmitter: ((SensorEvent) -> Unit) = { }

    init {
        navigationSensorManager.start { event ->
            eventEmitter.invoke(event)
        }
    }

    override fun onCleared() {
        navigationSensorManager.stop()
        eventEmitter = { }

        super.onCleared()
    }
}

