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
import com.example.sensorevaluation.R.color.abc_primary_text_disable_only_material_dark
import com.example.sensorevaluation.R.color.purple_500
import com.example.sensorevaluation.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import android.widget.ProgressBar
import android.view.View
import com.example.sensorevaluation.R.id.lineChartzacc

import java.io.File
import java.security.KeyStore


class MainActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent
    private val acc_x_Name : String = "acc_x.csv"
    private val acc_y_Name : String = "acc_y.csv"
    private val acc_z_Name : String = "acc_z.csv"
    private val gyro_x_Name : String = "gyro_x.csv"
    private val gyro_y_Name : String = "gyro_y.csv"
    private val gyro_z_Name : String = "gyro_z.csv"
    private lateinit var path : String
    private var index : Array<Int> = arrayOf(0)
    private lateinit var lineChart: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var lineChartxacc: LineChart
    private lateinit var lineChartyacc: LineChart
    private lateinit var lineChartzacc: LineChart
    private lateinit var lineChartxgyro: LineChart
    private lateinit var lineChartygyro: LineChart
    private lateinit var lineChartzgyro: LineChart
    private lateinit var linedatas : ArrayList<LineDataSet>
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = findViewById<ProgressBar>(R.id.progressBar3) as ProgressBar

        binding.Start.setOnClickListener { startMeasurment() }
        binding.Stop.setOnClickListener { stopMeasurment() }
        binding.ShowData.setOnClickListener { showData() }
        binding.ResetFile.setOnClickListener { reseteData() }
        serviceIntent = Intent(applicationContext, AccelerationService::class.java)
        lineChart = findViewById(R.id.lineChart)
        lineChart2 = findViewById(R.id.lineChart2)
        lineChartxacc = findViewById(R.id.lineChartxacc)
        lineChartyacc = findViewById(R.id.lineChartyacc)
        lineChartzacc = findViewById(R.id.lineChartzacc)
        lineChartxgyro = findViewById(R.id.lineChartgyrox)
        lineChartygyro = findViewById(R.id.lineChartgyroy)
        lineChartzgyro = findViewById(R.id.lineChartgyroz)


    }

    private fun startMeasurment() {
        progressBar?.visibility = View.VISIBLE
        startService(serviceIntent)
    }

    private fun stopMeasurment() {
        progressBar?.visibility = View.INVISIBLE
        stopService(serviceIntent)
    }

    private fun showData() {
        //list(fileNameAcc)
        //readFileLineByLineUsingForEachLine(fileNameAcc)
        if (findData()){
            linedatas = arrayListOf()
            findTuples()
            plotData(1)
        }else{
            Log.d("Main", "No data available.")
        }

    }

    private fun reseteData() {

        var fileExists = findData()
        if (fileExists){
            var nameArray = arrayOf(acc_x_Name, acc_y_Name, acc_z_Name, gyro_x_Name, gyro_y_Name, gyro_z_Name)
            for (i in nameArray){
                var file = openFile(i)
                file.delete()
                Log.d("CheckOnFile", "Deleted")
            }
        }
    }

    private fun findData() : Boolean{
        var fileExists : Boolean = false
        var file : File
        for (i in arrayOf(acc_x_Name, acc_y_Name, acc_z_Name, gyro_x_Name, gyro_y_Name, gyro_z_Name) ) {
            file = openFile(i)
            fileExists = file.exists()
        }
        return fileExists
    }

    private fun openFile(name: String) : File{
        var file = File("/data/user/0/com.example.sensorevaluation/files/" + "/" + name)
        return file
    }

    private fun findTuples(){
        var csvContent : String
        var parts : List<String>
        var count = 0
        arrayOf(acc_x_Name, acc_y_Name, acc_z_Name, gyro_x_Name, gyro_y_Name, gyro_z_Name)
        for (i in arrayOf(acc_x_Name, acc_y_Name, acc_z_Name, gyro_x_Name, gyro_y_Name, gyro_z_Name) ){
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
        var nameString = arrayOf("X-Accel.", "Y-Accel.","Z-Accel.","X-Gyro.","Y-Gyro.","Z-Gyro.")
        val linedataset = LineDataSet(lineentry, nameString[counts])

        var colorselect = arrayOf(R.color.darkRed, R.color.black, R.color.grey)
        if ((counts + 1 )== 3 || (counts + 1 )== 6){
            linedataset.color = resources.getColor(colorselect[2])
        }else if((counts + 1 )== 2 || (counts + 1 )== 5){
            linedataset.color = resources.getColor(colorselect[1])
        }else{
            linedataset.color = resources.getColor(colorselect[0])
        }
        linedataset.setDrawCircles(false)

        linedatas.add(linedataset)


        """val data = LineData(linedataset)
        if (counts < 3 ){

            lineChart.data = data
            lineChart.setBackgroundColor(resources.getColor(R.color.greishTrans))
            lineChart.extraRightOffset
            lineChart.animateXY(3000, 3000)
        }else if(counts >= 3){
            lineChart2.data = data
            lineChart2.setBackgroundColor(resources.getColor(R.color.greishTrans))
            lineChart2.animateXY(3000, 3000)
        }"""
    }

    private fun plotData(counts: Int){
        var data = LineData(linedatas.get(0),linedatas.get(1), linedatas.get(2))

        lineChart.data = data
        lineChart.setBackgroundColor(resources.getColor(R.color.greishTrans))
        lineChart.setGridBackgroundColor(R.color.grey)
        lineChart.setNoDataText("Data not yet available")
        lineChart.setNoDataTextColor(R.color.grey)
        lineChart.animateXY(1000, 3000)

        data = LineData(linedatas.get(3),linedatas.get(4), linedatas.get(5))
        lineChart2.data = data
        lineChart2.setBackgroundColor(resources.getColor(R.color.greishTrans))
        lineChart2.animateXY(3000, 3000)

        data = LineData(linedatas.get(0))
        lineChartxacc.data = data
        lineChartxacc.setBackgroundColor(resources.getColor(R.color.greishTrans))
        lineChartxacc.animateXY(3000, 3000)


        data = LineData(linedatas.get(1))
        lineChartyacc.data = data
        lineChartyacc.setBackgroundColor(resources.getColor(R.color.greishTrans))
        lineChartyacc.animateXY(3000, 3000)

        data = LineData(linedatas.get(2))
        lineChartzacc.data = data
        lineChartzacc.setBackgroundColor(resources.getColor(R.color.greishTrans))
        lineChartzacc.animateXY(3000, 3000)

        data = LineData(linedatas.get(3))
        lineChartxgyro.data = data
        lineChartxgyro.setBackgroundColor(resources.getColor(R.color.greishTrans))
        lineChartxgyro.animateXY(3000, 3000)

        data = LineData(linedatas.get(4))
        lineChartygyro.data = data
        lineChartygyro.setBackgroundColor(resources.getColor(R.color.greishTrans))
        lineChartygyro.animateXY(3000, 3000)

        data = LineData(linedatas.get(5))
        lineChartzgyro.data = data
        lineChartzgyro.setBackgroundColor(resources.getColor(R.color.greishTrans))
        lineChartzgyro.animateXY(3000, 3000)

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

