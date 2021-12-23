package com.example.sensorevaluation

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kotlin.random.Random

class AccelerationService : Service(), SensorEventListener
{
    override fun onBind(intent: Intent?): IBinder? = null
    private var mSensorManager : SensorManager? = null
    private lateinit var path : String

    //File Names
    private val acc_x_Name : String? = "acc_x.csv"
    private val acc_y_Name : String? = "acc_y.csv"
    private val acc_z_Name : String? = "acc_z.csv"
    private val gyro_x_Name : String? = "gyro_x.csv"
    private val gyro_y_Name : String? = "gyro_y.csv"
    private val gyro_z_Name : String? = "gyro_z.csv"

    //Files
    private lateinit var fileAcc_x: FileOutputStream
    private lateinit var fileAcc_y: FileOutputStream
    private lateinit var fileAcc_z: FileOutputStream
    private lateinit var fileGyro_x: FileOutputStream
    private lateinit var fileGyro_y: FileOutputStream
    private lateinit var fileGyro_z: FileOutputStream

    private var num : Double = 0.0

    //private var fileAcc = File(fileNameAcc)

    private var TAG = "AccelerationService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        path = filesDir.absolutePath
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager


        //Register accelerometer
        mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            mSensorManager!!.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        //Register gyroscope
        mSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also { gyroscope ->
            mSensorManager!!.registerListener(
                this,
                gyroscope,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        for(i in arrayOf(acc_x_Name, gyro_x_Name)){
            checkFileexistense(path, i)
        }


        """var file = File(path + "/" +acc_x_Name)
        var fileExists = file.exists()
        if (fileExists){
            file.delete()
            Log.d("CheckOnFile", "Deleted")
        }"""


        //fileAcc = File(fileNameAcc)
        //val fileAccCreated : Boolean = fileAcc?.createNewFile() ?: false
        //Log.d(TAG, "AccFile has been created. " + fileAccCreated.toString())
        return START_STICKY
    }

    fun checkFileexistense(_path:String, name:String?){
        var file = File(path + "/" +acc_x_Name)
        var fileExists = file.exists()
        if (fileExists){
            //file.delete()
            Log.d("CheckOnFile", "Not yet deleted")
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        val current = System.currentTimeMillis()
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            fileGyro_x = openFileOutput(gyro_x_Name, Context.MODE_APPEND)
            fileGyro_y = openFileOutput(gyro_y_Name, Context.MODE_APPEND)
            fileGyro_z = openFileOutput(gyro_z_Name, Context.MODE_APPEND)

            fileGyro_x.write((event.values[0].toString() + ", " + current.toString()+ "; " ).toByteArray() )
            fileGyro_y.write((event.values[1].toString() + ", " + current.toString()+ "; " ).toByteArray())
            fileGyro_z.write((event.values[2].toString() + ", " + current.toString()+ "; " ).toByteArray())
            Log.d(TAG, "Gyro received.")
        }else if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER)
        {
            fileAcc_x = openFileOutput(acc_x_Name, Context.MODE_APPEND)
            fileAcc_y = openFileOutput(acc_y_Name, Context.MODE_APPEND)
            fileAcc_z = openFileOutput(acc_z_Name, Context.MODE_APPEND)

            //num = event.values[0] + Random.nextDouble(0.0, 30.0)



            fileAcc_x.write((event.values[0].toString() + ", " + current.toString()+ "; " ).toByteArray() )
            fileAcc_y.write((event.values[1].toString() + ", " + current.toString()+ "; " ).toByteArray())
            fileAcc_z.write((event.values[2].toString() + ", " + current.toString()+ "; " ).toByteArray())
            //fileAcc.writeText("ACC " + event.values[0].toString())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroy()
    {
        Log.d("SensorChange", "Stop")
        File("/data/user/0/com.example.sensorevaluation/files").list().forEach { println(it) }
        mSensorManager?.unregisterListener(this)
        super.onDestroy()
    }
}