package pnj.uas.ti.marwah_nur_shafira.fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pnj.uas.ti.marwah_nur_shafira.data.Wisata
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivityDetailDataBinding
import pnj.uas.ti.marwah_nur_shafira.sql.DatabaseOpenHelper

class DetailWisataFragment : Fragment() {
    private lateinit var binding: ActivityDetailDataBinding
    private lateinit var sqlLiteDatabase: SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ActivityDetailDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openDatabase()

        val bundle = activity?.intent?.extras
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

            binding.txtNama.text = wisata.name
            binding.txtTlp.text = wisata.tlp
            binding.txtTahun.text = wisata.tahun
            binding.txtLongitude.text = wisata.longitude
            binding.txtLatitude.text = wisata.latitude
        }

        closeDatabase()
    }

    private fun openDatabase(){
        sqlLiteDatabase = DatabaseOpenHelper(requireContext()).writableDatabase
    }

    private fun closeDatabase(){
        sqlLiteDatabase.close()
    }

    fun newInstance(data: Wisata): Fragment {
        val fragmentDemo = DetailWisataFragment()
        val args = Bundle()

        args.putString("nama", data.name)
        args.putString("tlp", data.tlp)
        args.putString("tahun", data.tahun)
        args.putString("longitude", data.longitude)
        args.putString("latitude", data.latitude)
        fragmentDemo.arguments = args
        return fragmentDemo
    }

}