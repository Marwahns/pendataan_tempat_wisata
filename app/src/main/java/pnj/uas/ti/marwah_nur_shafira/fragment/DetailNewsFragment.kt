package pnj.uas.ti.marwah_nur_shafira.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import pnj.uas.ti.marwah_nur_shafira.data.Article
import pnj.uas.ti.marwah_nur_shafira.databinding.FragmentDetailNewsBinding

class DetailNewsFragment : Fragment() {
    private lateinit var binding: FragmentDetailNewsBinding

    fun newInstance(data: Article): DetailNewsFragment {
        val fragmentDemo = DetailNewsFragment()
        val args = Bundle()

        args.putString("title", data.title)
        args.putString("img", data.urlToImage)
        args.putString("desc", data.description)
        fragmentDemo.arguments = args
        return fragmentDemo
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments

        // Panggil fungsi hideMyLocationButton() pada listener ketika ingin menyembunyikan tombol btnMyLocation
        (activity as? DetailNewsFragment.FragmentDetailNewsListener)?.hideMyLocationButton()

        Glide.with(requireActivity()).load(bundle?.getString("img","")).into(binding.imageView)
        binding.txtTitle.text = bundle?.getString("title", "")
        binding.txtDesc.text = bundle?.getString("desc", "")
    }

    interface FragmentDetailNewsListener {
        fun hideMyLocationButton()
    }
}