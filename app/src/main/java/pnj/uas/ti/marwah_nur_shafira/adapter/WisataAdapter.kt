package pnj.uas.ti.marwah_nur_shafira.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import pnj.uas.ti.marwah_nur_shafira.R
import pnj.uas.ti.marwah_nur_shafira.data.Wisata

class WisataAdapter(context: Context, resouce:Int) : ArrayAdapter<Wisata>(context, resouce){

    private var _context = context
    private var resouce: Int = 0
    init{
        this._context = context
        this.resouce = resouce
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder:Holder
        var viewItemData = convertView

        try {
            if(convertView==null){
                holder = Holder()

                viewItemData = LayoutInflater.from(context).inflate(resouce,parent,false)
                holder.txtNama = viewItemData.findViewById(R.id.txtWisata)
                holder.txtLocation = viewItemData.findViewById(R.id.txtLocation)
                holder.txtDistance = viewItemData.findViewById(R.id.txtDistance)

            } else{
                holder = viewItemData?.tag as Holder
                viewItemData = convertView
            }

            val data = getItem(position)

            holder.txtNama.text = data?.name
            holder.txtLocation.text = data?.location
            holder.txtDistance.text = getItem(position)?.distance + " Km"
        } catch (_:Exception){

        }
        return viewItemData!!
    }

    class Holder {
        lateinit var txtNama:TextView
        lateinit var txtLocation:TextView
        lateinit var txtDistance: TextView
    }
}