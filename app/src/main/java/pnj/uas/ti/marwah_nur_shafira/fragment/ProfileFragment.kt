package pnj.uas.ti.marwah_nur_shafira.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import pnj.uas.ti.marwah_nur_shafira.auth.SignInActivity
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivityFragmentBinding
import pnj.uas.ti.marwah_nur_shafira.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val sharedPreferences = requireActivity().getSharedPreferences("dataLogin", Context.MODE_PRIVATE)

        binding.txtEmail.text = sharedPreferences.getString("email", "")

        // Panggil fungsi hideMyLocationButton() pada listener ketika ingin menampilkan tombol btnMyLocation
        (activity as? FragmentProfileListener)?.hideMyLocationButton()

        binding.btnLogout.setOnClickListener {
            val edit = sharedPreferences.edit()
            edit.clear()
            edit.apply()
            activity?.finish()
            firebaseAuth.signOut()
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
        }
    }

    interface FragmentProfileListener {
        fun hideMyLocationButton()
    }

}