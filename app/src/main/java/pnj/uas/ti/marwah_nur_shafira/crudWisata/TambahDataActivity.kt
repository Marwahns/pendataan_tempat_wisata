package pnj.uas.ti.marwah_nur_shafira.crudWisata

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.openDatabase
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import pnj.uas.ti.marwah_nur_shafira.R
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivityTambahDataBinding
import pnj.uas.ti.marwah_nur_shafira.sql.DatabaseOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class TambahDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahDataBinding
    private lateinit var sqlLiteDatabase: SQLiteDatabase
    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtNoTlp.inputType = InputType.TYPE_CLASS_NUMBER
        binding.edtLongitude.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
        binding.edtLatitude.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
//        binding.edtTahunBeroperasi.inputType = InputType.TYPE_CLASS_NUMBER
        binding.edtTahunBeroperasi.isEnabled = false

        binding.btnSimpan.setOnClickListener{
            // isNotEmpty = apakah teks memiliki panjang lebih besar dari 0
            if(binding.edtNama.text.isNotEmpty() || binding.edtNoTlp.text.isNotEmpty() || binding.edtTahunBeroperasi.text.isNotEmpty() ||
                binding.edtLongitude.text.isNotEmpty() || binding.edtLatitude.text.isNotEmpty()
            ) {

                openDatabase()

                // Membuat objek ContentValues untuk menyimpan data
                val contentValue = ContentValues()
                contentValue.put("nama", binding.edtNama.text.toString())
                contentValue.put("no_tlp", binding.edtNoTlp.text.toString())
                contentValue.put("tahun", binding.edtTahunBeroperasi.text.toString())
                contentValue.put("longitude", binding.edtLongitude.text.toString())
                contentValue.put("latitude", binding.edtLatitude.text.toString())

                val insert:Long= sqlLiteDatabase.insert("tb_wisata", null, contentValue)

                if(!insert.equals(-1)){
                    Toast.makeText(this, "Data Added Successfully", Toast.LENGTH_SHORT).show()
                    binding.edtNama.setText("")
                    binding.edtNoTlp.setText("")
                    binding.edtTahunBeroperasi.setText("")
                    binding.edtLongitude.setText("")
                    binding.edtLatitude.setText("")
                    setResult(RESULT_OK)
                    finish()
                    val intent = Intent(this, ListDataWisataActivity::class.java)
                    startActivity(intent)
                } else{
                    Toast.makeText(this, "Failed Data Added", Toast.LENGTH_SHORT).show()
                }

                closeDatabase()
                
            }
        }

        binding.btnDate.setOnClickListener {
            showDatePicker()
        }

    }

    private fun showDatePicker(){
        DatePickerDialog(this, listenerDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
            Calendar.DAY_OF_MONTH)).show()
    }

    private var listenerDate = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val data = Calendar.getInstance()
        data.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        data.set(Calendar.MONTH, month)
        data.set(Calendar.YEAR, year)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.edtTahunBeroperasi.setText((dateFormat.format(data.time)))
    }

    private fun getCityName(lat: Double,long: Double):String{
        val cityName: String
        val countryName: String
        val geoCoder = Geocoder(this, Locale.getDefault())
        @Suppress("DEPRECATION") val address = geoCoder.getFromLocation(lat,long,3)

        cityName = address!![0].locality
        countryName = address[0].countryName
        Log.d("Debug:", "Your City: $cityName ; your Country $countryName")
        return cityName
    }

    private fun openDatabase(){
        sqlLiteDatabase = DatabaseOpenHelper(this).writableDatabase
    }

    private fun closeDatabase(){
        sqlLiteDatabase.close()
    }
}