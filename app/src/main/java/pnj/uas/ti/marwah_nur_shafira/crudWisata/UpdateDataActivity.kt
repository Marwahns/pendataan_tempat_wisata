package pnj.uas.ti.marwah_nur_shafira.crudWisata

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import pnj.uas.ti.marwah_nur_shafira.R
import pnj.uas.ti.marwah_nur_shafira.crudWisata.DetailDataActivity.Companion.isDataUpdated
import pnj.uas.ti.marwah_nur_shafira.data.Wisata
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivityUpdateDataBinding
import pnj.uas.ti.marwah_nur_shafira.sql.DatabaseOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class UpdateDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateDataBinding
    private lateinit var sqlLiteDatabase: SQLiteDatabase
    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()

        binding.edtNoTlp.inputType = InputType.TYPE_CLASS_NUMBER
        binding.edtLongitude.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
        binding.edtLatitude.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
//        binding.edtTahunBeroperasi.inputType = InputType.TYPE_CLASS_NUMBER
        binding.edtTahunBeroperasi.isEnabled = false

        if (binding.edtNama.text.toString() == "" || binding.edtNoTlp.text.toString() == "" ||
            binding.edtTahunBeroperasi.text.toString() == "" || binding.edtLongitude.text.toString() == "" ||
            binding.edtLatitude.text.toString() == ""){
            binding.btnSimpan.setBackgroundColor(resources.getColor(R.color.login_email_invalid))
            binding.btnSimpan.setTextColor(resources.getColor(R.color.white))
            binding.btnSimpan.isEnabled = false
        }

        binding.btnSimpan.setOnClickListener{
            if(binding.edtNama.text.isNotEmpty() || binding.edtNoTlp.text.isNotEmpty() || binding.edtTahunBeroperasi.text.isNotEmpty() ||
                binding.edtLongitude.text.isNotEmpty() || binding.edtLatitude.text.isNotEmpty()
            ) {

                openDatabase()

                val bundle = intent.extras
                var id = 0
                if(bundle!=null){
                    id = bundle.getInt("id")
                }

                val contentValue = ContentValues()
                contentValue.put("nama", binding.edtNama.text.toString())
                contentValue.put("no_tlp", binding.edtNoTlp.text.toString())
                contentValue.put("tahun", binding.edtTahunBeroperasi.text.toString())
                contentValue.put("longitude", binding.edtLongitude.text.toString())
                contentValue.put("latitude", binding.edtLatitude.text.toString())

                val update = sqlLiteDatabase.update("tb_wisata", contentValue, "id=$id", null)

                if(update != -1){
                    Toast.makeText(this, "Data Successfully Changed", Toast.LENGTH_SHORT).show()

                    // Set the result as RESULT_OK and indicate that the data has been changed
                    isDataUpdated = true
                    val resultIntent = Intent()
                    resultIntent.putExtra("dataChanged", true)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else{
                    Toast.makeText(this, "Data Failed to Update", Toast.LENGTH_SHORT).show()
                }

                closeDatabase()

            }
        }

        binding.btnDate.setOnClickListener {
            showDatePicker()
        }

    }

    private fun showDatePicker(){
        DatePickerDialog(this, listenerDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private var listenerDate = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val data = Calendar.getInstance()
        data.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        data.set(Calendar.MONTH, month)
        data.set(Calendar.YEAR, year)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.edtTahunBeroperasi.setText((dateFormat.format(data.time)))
    }

    private fun setData(){
        openDatabase()

        val bundle = intent.extras
        var id = 0
        if(bundle!=null){
            id = bundle.getInt("id")
        }
        val cursor = sqlLiteDatabase.rawQuery("SELECT * FROM tb_wisata WHERE id=$id",null)

        if (cursor.moveToFirst()) {
            val wisata = Wisata()
            wisata.id = cursor.getInt(0)
            wisata.name = cursor.getString(1)
            wisata.tlp = cursor.getString(2)
            wisata.tahun = cursor.getString(3)
            wisata.longitude = cursor.getString(4)
            wisata.latitude = cursor.getString(5)

            binding.edtNama.setText(wisata.name)
            binding.edtNoTlp.setText(wisata.tlp)
            binding.edtTahunBeroperasi.setText(wisata.tahun)
            binding.edtLongitude.setText(wisata.longitude)
            binding.edtLatitude.setText(wisata.latitude)
        }
        else {
            // Jika cursor kosong (data tidak ditemukan), berarti data dengan ID tersebut tidak ada.
            // Karena itu, kembali ke halaman ListDataAlumniActivity.
            setResult(RESULT_CANCELED)
            finish()
        }


        closeDatabase()
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
        val intent = Intent(this, ListDataWisataActivity::class.java)
        startActivity(intent)
        finish()
    }

}