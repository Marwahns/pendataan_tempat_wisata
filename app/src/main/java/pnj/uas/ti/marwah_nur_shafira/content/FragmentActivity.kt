package pnj.uas.ti.marwah_nur_shafira.content

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import pnj.uas.ti.marwah_nur_shafira.R
import pnj.uas.ti.marwah_nur_shafira.auth.SignInActivity
import pnj.uas.ti.marwah_nur_shafira.crudWisata.ListDataWisataActivity
import pnj.uas.ti.marwah_nur_shafira.crudWisata.TambahDataActivity
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivityFragmentBinding
import pnj.uas.ti.marwah_nur_shafira.fragment.ArticleFragment
import pnj.uas.ti.marwah_nur_shafira.fragment.DetailNewsFragment
import pnj.uas.ti.marwah_nur_shafira.fragment.HomeFragment
import pnj.uas.ti.marwah_nur_shafira.fragment.ProfileFragment
import pnj.uas.ti.marwah_nur_shafira.maps.CheckLocationActivity
import java.util.*

@Suppress("DEPRECATION")
class FragmentActivity : AppCompatActivity(), LocationListenerCompat,
    ProfileFragment.FragmentProfileListener, ArticleFragment.FragmentArticleListener,
    DetailNewsFragment.FragmentDetailNewsListener, HomeFragment.FragmentHomeListener {
    private lateinit var binding: ActivityFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val REQUEST_CODE_ADD_DATA = 100

    private var lat = 0.0
    private var lng = 0.0
    private var minTime: Long = 0
    private var minDistance: Float = 0f
    private var locProvider: String? = null
    private lateinit var locMgr: LocationManager
    private lateinit var lastKnownLocation: Location

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        replaceFragment(HomeFragment())
        requestPermission()

        locMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locProvider = LocationManager.GPS_PROVIDER

        lastKnownLocation = locMgr.getLastKnownLocation(locProvider!!) ?: Location("Default").apply {
            latitude = 0.0
            longitude = 0.0
        }

        lat = lastKnownLocation.latitude
        lng = lastKnownLocation.longitude

        val cr = Criteria()
        cr.accuracy = Criteria.ACCURACY_FINE
        locProvider = locMgr.getBestProvider(cr, false)
        minTime = 10 * 1000
        minDistance = 10f

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.news -> replaceFragment(ArticleFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
            }

            true
        }

        binding.btnMyLocation.setOnClickListener {
            val intent = Intent(this, CheckLocationActivity::class.java)
            // Mengirimkan data latitude dan longitude ke MapsActivity2
            intent.putExtra("MYLATITUDE", lat)
            intent.putExtra("MYLONGITUDE", lng)
            startActivity(intent)
        }

        Log.e("City: ", "" + getCityName(lat, lng))
    }

    private fun replaceFragment (fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    // Method untuk memunculkan 'item' pada menu option
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuOption = menuInflater.inflate(R.menu.menu_app, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_tambah -> {
                val intent = Intent(this, TambahDataActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_ADD_DATA)
            }
            R.id.menu_lihat -> {
                val intent = Intent(this, ListDataWisataActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_logout -> {
                val data = getSharedPreferences("dataLogin", Context.MODE_PRIVATE)
                val edit = data.edit()
                edit.clear()
                edit.apply()
                finish()
                firebaseAuth.signOut()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Kalau sudah sign out, pas back ga akan balik ke Main Activity, tetapi harus di sign in
    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser == null){
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
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

    private fun getCityName(lat: Double, long: Double): String {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addressList = geoCoder.getFromLocation(lat, long, 3)

        return if (addressList.isNullOrEmpty()) {
            // Jika daftar alamat kosong atau null, berikan nilai default atau tampilkan pesan kesalahan
            "Unknown City"
        } else {
            val cityName = addressList[0].locality
            val countryName = addressList[0].countryName
            Log.d("Debug:", "Your City: $cityName ; Your Country $countryName")
            cityName ?: "Unknown City"
        }
    }

    override fun showMyLocationButton() {
        binding.btnMyLocation.visibility = View.VISIBLE
    }

    override fun hideMyLocationButton() {
        binding.btnMyLocation.visibility = View.INVISIBLE
    }

}
