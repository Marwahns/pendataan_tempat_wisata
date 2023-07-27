package pnj.uas.ti.marwah_nur_shafira.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivityFragmentBinding
import pnj.uas.ti.marwah_nur_shafira.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Panggil fungsi hideMyLocationButton() pada listener ketika ingin menyembunyikan tombol btnMyLocation
        (activity as? HomeFragment.FragmentHomeListener)?.showMyLocationButton()
    }

    interface FragmentHomeListener {
        fun showMyLocationButton()
    }

}