package pnj.uas.ti.marwah_nur_shafira.crudWisata

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import pnj.uas.ti.marwah_nur_shafira.data.Wisata
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivityDetailDataBinding
import pnj.uas.ti.marwah_nur_shafira.sql.DatabaseOpenHelper
import java.util.*

@Suppress("DEPRECATION")
class DetailDataActivity : AppCompatActivity() {
    private lateinit var sqlLiteDatabase: SQLiteDatabase
    private lateinit var binding: ActivityDetailDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()

        binding.imageButtonEdit.setOnClickListener {
            getData()
        }

        binding.btnDelete.setOnClickListener {
            openDatabase()

            val bundle = intent.extras
            var id = 0
            if(bundle!=null){
                id = bundle.getInt("id")
            }

            val delete = sqlLiteDatabase.delete("tb_wisata", "id=$id", null)

            if(delete != -1){
                Toast.makeText(this, "Successfully Delete Data", Toast.LENGTH_SHORT).show()
                binding.txtNama.text = ""
                binding.txtTlp.text = ""
                binding.txtTahun.text = ""
                binding.txtLongitude.text = ""
                binding.txtLatitude.text = ""
                setResult(RESULT_OK)
                finish()
                val intent = Intent(this, ListDataWisataActivity::class.java)
                startActivity(intent)
            } else{
                Toast.makeText(this, "Data Failed to Delete", Toast.LENGTH_SHORT).show()
            }

            closeDatabase()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT) {
            if (resultCode == RESULT_OK) {
                // Periksa apakah data telah diubah
                val dataChanged = data?.getBooleanExtra("dataChanged", false) ?: false
                if (dataChanged) {
                    // Jika data telah diubah, tampilkan data terbaru
                    setData()
                }
            }
        }
    }

    private fun getData() {
        openDatabase()
        val bundle = intent.extras
        var id = 0
        if (bundle != null) {
            id = bundle.getInt("id")
        }
        val cursor = sqlLiteDatabase.rawQuery("SELECT * FROM tb_wisata WHERE id=$id", null)

        if (cursor.moveToFirst()) {
            val wisata = Wisata()
            wisata.id = cursor.getInt(0)
            wisata.name = cursor.getString(1)
            wisata.tlp = cursor.getString(2)
            wisata.tahun = cursor.getString(3)
            wisata.longitude = cursor.getString(4)
            wisata.latitude = cursor.getString(5)

            val intent = Intent(applicationContext, UpdateDataActivity::class.java)
            intent.putExtra("id", wisata.id)
            startActivityForResult(intent, REQUEST_CODE_EDIT) // Start activity for result
        }

        closeDatabase()
    }

    private fun setData(){
        openDatabase()

        val bundle = intent.extras
        var id = 0
        if(bundle!=null){
            id = bundle.getInt("id")
        }
        val cursor = sqlLiteDatabase.rawQuery("SELECT * FROM tb_wisata WHERE id=$id",null)

        if(cursor.moveToFirst()){
            val wisata = Wisata()
            wisata.id = cursor.getInt(0)
            wisata.name = cursor.getString(1)
            wisata.tlp = cursor.getString(2)
            wisata.tahun = cursor.getString(3)
            wisata.longitude = cursor.getString(4)
            wisata.latitude = cursor.getString(5)

            val cityName = getCityName(wisata.latitude.toDouble(), wisata.longitude.toDouble())
            wisata.location = cityName

            binding.txtNama.text = wisata.name
            binding.txtTlp.text = wisata.tlp
            binding.txtTahun.text = wisata.tahun
            binding.txtLongitude.text = wisata.longitude
            binding.txtLatitude.text = wisata.latitude
            binding.txtLocation.text = wisata.location
        }

        closeDatabase()
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

    companion object {
        private const val REQUEST_CODE_EDIT = 102
        var isDataUpdated = false
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Override the back button behavior to go to the main page
        val intent = Intent(this, ListDataWisataActivity::class.java)
        startActivity(intent)
        finish()
    }

}