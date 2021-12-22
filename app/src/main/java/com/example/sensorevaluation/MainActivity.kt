package com.example.sensorevaluation
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.DropBoxManager
import android.os.SystemClock
import android.util.Log
import android.util.Size
import android.view.Gravity.apply
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.sensorevaluation.R.color.purple_500
import com.example.sensorevaluation.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.charts.LineChart
import java.io.File
import java.security.KeyStore


class MainActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent
    private val fileNameAcc_x: String = "acc_x.csv"
    private val fileNameGyro_x: String = "gyro_x.csv"
    private lateinit var path : String
    private var index : Array<Int> = arrayOf(0)
    private lateinit var lineChart: LineChart
    private lateinit var lineChart2: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Start.setOnClickListener { startMeasurment() }
        binding.Stop.setOnClickListener { stopMeasurment() }
        binding.ShowData.setOnClickListener { showData() }
        serviceIntent = Intent(applicationContext, AccelerationService::class.java)
        lineChart = findViewById(R.id.lineChart)
        lineChart2 = findViewById(R.id.lineChart2)
    }

    private fun startMeasurment() {
        startService(serviceIntent)
    }

    private fun stopMeasurment() {
        stopService(serviceIntent)
    }

    private fun showData() {
        //list(fileNameAcc)
        //readFileLineByLineUsingForEachLine(fileNameAcc)
        findTuples()
    }

    private fun findTuples(){
        var csvContent : String
        var parts : List<String>
        var count = 0
        arrayOf(fileNameAcc_x, fileNameGyro_x)
        for (i in arrayOf(fileNameAcc_x, fileNameGyro_x) ){
            csvContent = File("/data/user/0/com.example.sensorevaluation/files/" + i).readText()
            parts = csvContent.split(";")
            setLineChartData(parts, count)
            count = count + 1
        }
    }

    fun append(arr: Array<Int>, element: Int): Array<Int> {
        val list: MutableList<Int> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setLineChartData(values:List<String>, counts:Int){
        val xvalue = ArrayList<Float>()
        val lineentry = ArrayList<Entry>()
        var parts : List<String>
        var count = 0
        var firstTimeStamp = 0.0
        for (i in values){
            parts = i.split(",")
            if (parts.size == 2){
                if (count == 0){
                    firstTimeStamp = parts[1].toDouble()
                }
                //xvalue.add(Entry(parts[1].toFloat(), count.toFloat()))
                //lineentry.add(Entry(parts[0].toFloat(), count.toFloat()))
                lineentry.add(Entry(((parts[1].toDouble() - firstTimeStamp)/1000).toFloat(), parts[0].toFloat()))
                count = count + 1
                xvalue.add((parts[1].toDouble() - firstTimeStamp).toFloat())
            }

        }
        var nameString = arrayOf("X-Accel.", "X-Gyro.")
        val linedataset = LineDataSet(lineentry, nameString[counts])

        //val lineDataSet_x = LineDataSet(xvalue, "Second")
        linedataset.color = resources.getColor(R.color.darkRed)

        val data = LineData(linedataset)
        if (counts == 0){
            lineChart.data = data
            lineChart.setBackgroundColor(resources.getColor(R.color.greishTrans))
            lineChart.extraRightOffset
            lineChart.animateXY(3000, 3000)
        }else if(counts ==1){
            lineChart2.data = data
            lineChart2.setBackgroundColor(resources.getColor(R.color.greishTrans))
            lineChart2.animateXY(3000, 3000)
        }




    }




    fun readFileLineByLineUsingForEachLine(fileName: String)
    = File("/data/user/0/com.example.sensorevaluation/files/"  + fileName).forEachLine { println(it) }


    fun list(fileName: String) {
        val csvContent = File("/data/user/0/com.example.sensorevaluation/files/" + "acc_x.csv").readText()

        //println("Loaded ${data.size} records")
        //temp("/data/user/0/com.example.sensorevaluation/files/" + fileName)
    }

    data class Data(
        val value: Double,
        val time: Double
    )


    fun temp(name : String)
        = File(name).forEachLine { println(it) }


}

