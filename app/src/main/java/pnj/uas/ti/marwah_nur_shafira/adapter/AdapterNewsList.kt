package pnj.uas.ti.marwah_nur_shafira.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import pnj.uas.ti.marwah_nur_shafira.R
import pnj.uas.ti.marwah_nur_shafira.data.Article

class AdapterNewsList(context: Context, resource: Int):
    ArrayAdapter<Article>(context, resource) {

    private var _context = context
    private var _resource = resource

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Update isi-isi dari item
        val holder:Holder
        lateinit var viewItem: View

        if(convertView==null){
            holder = Holder()
            viewItem = LayoutInflater.from(_context).inflate(_resource, null, false)
            holder.imageView = viewItem.findViewById(R.id.imageView)
            holder.txtTitle = viewItem.findViewById(R.id.txtTitle)
            viewItem.tag = holder

        }else{
            holder = convertView.tag as Holder
            viewItem = convertView
        }

        holder.txtTitle.text = getItem(position)?.title
        Glide.with(_context).load(getItem(position)?.urlToImage).into(holder.imageView)

        return viewItem
    }

    // Inner class Holder
    class Holder {
        lateinit var imageView: ImageView
        lateinit var txtTitle: TextView
    }
}