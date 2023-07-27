package pnj.uas.ti.marwah_nur_shafira.crudWisata

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.icu.text.DecimalFormat
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import pnj.uas.ti.marwah_nur_shafira.R
import pnj.uas.ti.marwah_nur_shafira.adapter.WisataAdapter
import pnj.uas.ti.marwah_nur_shafira.content.FragmentActivity
import pnj.uas.ti.marwah_nur_shafira.data.Wisata
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivityListDataWisataBinding
import pnj.uas.ti.marwah_nur_shafira.sql.DatabaseOpenHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ListDataWisataActivity : AppCompatActivity(), LocationListenerCompat {
    private lateinit var binding: ActivityListDataWisataBinding
    private lateinit var sqlLiteDatabase: SQLiteDatabase
    private lateinit var adapter: WisataAdapter

    private var lat = 0.0
    private var lng = 0.0
    private var minTime: Long = 0
    private var minDistance: Float = 0f
    private var locProvider: String? = null
    private lateinit var locMgr: LocationManager
    private lateinit var lastKnownLocation: Location

    private val updateDataLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            getData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDataWisataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = WisataAdapter(this, R.layout.layout_item)
        binding.listView.adapter = adapter

        requestPermission()
        getData()

        locMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locProvider = LocationManager.GPS_PROVIDER

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        lastKnownLocation = locMgr.getLastKnownLocation(locProvider!!)!!

        lat = lastKnownLocation.latitude
        lng = lastKnownLocation.longitude

        val cr = Criteria()
        cr.accuracy = Criteria.ACCURACY_FINE
        locProvider = locMgr.getBestProvider(cr, false)
        minTime = 10 * 1000
        minDistance = 10f

        binding.listView.setOnItemClickListener { parent, _, position, _ ->
            val wisata = parent.adapter.getItem(position) as Wisata
            val intent = Intent(applicationContext, DetailDataActivity::class.java)
            intent.putExtra("id", wisata.id)
            updateDataLauncher.launch(intent)
        }
    }

    // on resume itu untuk refresh terus data
    override fun onResume() {
        super.onResume()
        getData()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locMgr.requestLocationUpdates(locProvider!!, minTime, minDistance,this)
    }

    override fun onLocationChanged(location: Location) {
        // Know every location change is here
        Log.e("Latitude: ", "" + location.latitude)
        Log.e("Longitude: ", "" + location.longitude)
    }


    private fun requestPermission(){
        val strPermission1 = Manifest.permission.ACCESS_FINE_LOCATION
        val strPermission2 = Manifest.permission.ACCESS_COARSE_LOCATION
        val grant1 = ContextCompat.checkSelfPermission(this, strPermission1)
        val grant2 = ContextCompat.checkSelfPermission(this, strPermission2)
        if(grant1 != PackageManager.PERMISSION_GRANTED && grant2 != PackageManager.PERMISSION_GRANTED){
            val permissions = arrayOf(strPermission1, strPermission2)
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    private fun getData() {
        openDatabase()
        val cursor = sqlLiteDatabase.rawQuery("SELECT * FROM tb_wisata", null)
        val data = ArrayList<Wisata>()
        if (cursor.moveToFirst()) {
            do {
                val wisata = Wisata()
                wisata.id = cursor.getInt(0)
                wisata.name = cursor.getString(1)
                wisata.tlp = cursor.getString(2)
                wisata.tahun = cursor.getString(3)
                wisata.longitude = cursor.getString(4)
                wisata.latitude = cursor.getString(5)

                // Check if latitude and longitude are not empty
                if (wisata.latitude.isNotEmpty() && wisata.longitude.isNotEmpty()) {
                    val cityName = getCityName(wisata.latitude.toDouble(), wisata.longitude.toDouble())
                    if (cityName.isNotEmpty()) {
                        wisata.location = cityName
                    } else {
                        Log.e("getData", "City name not found for latitude: ${wisata.latitude}, longitude: ${wisata.longitude}")
                        // Handle the case where city name is not found
                    }

                    val jarak: Double = getDistance(wisata.latitude.toDouble(), wisata.longitude.toDouble(), lat, lng)
                    val decimalFormat = DecimalFormat("#.##")
                    decimalFormat.maximumFractionDigits = 2
                    val formattedNumber = decimalFormat.format(jarak)

                    wisata.distance = formattedNumber.toString()

                    Log.e("getData", "City name not found for latitude: ${wisata.latitude}, longitude: ${wisata.longitude}")

                    data.add(wisata)
                }
            } while (cursor.moveToNext())

            adapter.clear()
            adapter.addAll(data)
            adapter.notifyDataSetChanged()
        }

        closeDatabase()
    }

    private fun getDistance(
        latitudeTujuan: Double,
        longitudeTujuan: Double,
        latitudeUser: Double,
        longitudeUser: Double
    ): Double {
        /* VARIABLE */
        val pi = 3.14159265358979
        //        val R = 6371e3
        val r = 6371.0 // Radius of the Earth in kilometers

        val latRadTujuan = latitudeTujuan * (pi / 180)
        val latRadUser = latitudeUser * (pi / 180)
        val deltaLatRad = (latitudeUser - latitudeTujuan) * (pi / 180)
        val deltaLonRad = (longitudeUser - longitudeTujuan) * (pi / 180)

        /* RUMUS HAVERSINE */
        val a =
            sin(deltaLatRad / 2) * sin(deltaLatRad / 2) + cos(latRadTujuan) * cos(latRadUser) * sin(
                deltaLonRad / 2
            ) * sin(deltaLonRad / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun getCityName(lat: Double, long: Double): String {
        if (lat < -90.0 || lat > 90.0 || long < -180.0 || long > 180.0) {
            // Handle the case where latitude or longitude is out of range
            Log.e("getCityName", "Invalid latitude or longitude: lat=$lat, long=$long")
            return ""
        }

        val cityName: String
        val countryName: String
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addressList = geoCoder.getFromLocation(lat, long, 3)

        if (addressList != null) {
            if (addressList.isNotEmpty()) {
                cityName = addressList[0].locality ?: ""
                countryName = addressList[0].countryName ?: ""
                Log.d("Debug:", "Your City: $cityName; Your Country: $countryName")
                return cityName
            }
        }

        Log.e("getCityName", "City not found for latitude: $lat, longitude: $long")
        return ""
    }


    private fun openDatabase(){
        sqlLiteDatabase = DatabaseOpenHelper(this).writableDatabase
    }

    private fun closeDatabase(){
        sqlLiteDatabase.close()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Override the back button behavior to go to the main page
        val intent = Intent(this, FragmentActivity::class.java)
        startActivity(intent)
        finish()
    }
}