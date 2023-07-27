package pnj.uas.ti.marwah_nur_shafira.auth

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import pnj.uas.ti.marwah_nur_shafira.R
import pnj.uas.ti.marwah_nur_shafira.content.FragmentActivity
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivitySignUpBinding
import pnj.uas.ti.marwah_nur_shafira.fragment.HomeFragment

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var data: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvSignin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        if (binding.txtEmail.text.toString() == "" || binding.txtPass.text.toString() == "" || binding.txtPassConfirm.text.toString() == ""){
            binding.btnSignup.setBackgroundColor(resources.getColor(R.color.login_email_invalid))
            binding.btnSignup.setTextColor(resources.getColor(R.color.white))
            binding.tvConfirmPassword.visibility = View.INVISIBLE
            binding.btnSignup.isEnabled = false
        }

        // Email Address
        binding.txtEmail.doOnTextChanged { _, _, _, _ ->
            if (Patterns.EMAIL_ADDRESS.matcher(binding.txtEmail.text.toString()).matches()) {
                binding.txtEmail.setBackgroundResource(R.drawable.rounded_border_edit_text)
            } else {
                binding.txtEmail.setBackgroundResource(R.drawable.rounded_warning_border_edit_text)
                binding.txtEmail.error = "Invalid Email Address"
            }
        }

        // Password Confirm
        binding.txtPassConfirm.doOnTextChanged { _, _, _, _ ->
            if (binding.txtPassConfirm.text.toString() != binding.txtPass.text.toString()) {
                binding.tvConfirmPassword.visibility = View.VISIBLE
                binding.btnSignup.setBackgroundColor(resources.getColor(R.color.login_email_invalid))
                binding.btnSignup.setTextColor(resources.getColor(R.color.white))
                binding.btnSignup.isEnabled = false
            } else {
                binding.tvConfirmPassword.visibility = View.INVISIBLE
                binding.btnSignup.setBackgroundColor(resources.getColor(R.color.login))
                binding.btnSignup.setTextColor(resources.getColor(R.color.white))
                binding.btnSignup.isEnabled = true
            }
        }

        // Show Hide Password
        binding.showPassword.setOnCheckedChangeListener { _, _ ->
            if (binding.txtPass.transformationMethod.equals(HideReturnsTransformationMethod.getInstance()) || binding.txtPassConfirm.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                // If password is visible then Hide it
                binding.txtPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.txtPassConfirm.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                binding.txtPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.txtPassConfirm.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        // Button Sign Up
        binding.btnSignup.setOnClickListener {
            val email: String = binding.txtEmail.text.toString()
            val password: String = binding.txtPass.text.toString()
            val confirmPass: String = binding.txtPassConfirm.text.toString()

            signupFirebase(email, password, confirmPass)
        }
    }

    private fun signupFirebase(email: String, password: String, confirm_pass:String){
        if(email.isNotEmpty() && password.isNotEmpty() && confirm_pass.isNotEmpty()){
            if(password == confirm_pass){
                // Pendaftaran user (Sign Up)
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        data = getSharedPreferences("dataLogin", MODE_PRIVATE)
                        val edit = data.edit()
                        edit.putString("email", email)
                        edit.apply()
                        finish()

                        // Pindah ke halaman apa?
                        // Pindah ke halaman main (yang perlu login)
                        val intent = Intent(this, FragmentActivity::class.java)
                        startActivity(intent)
                    } else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else{
                Toast.makeText(this, "Samakan Password dan Konfirmasi Password", Toast.LENGTH_SHORT).show()
            }
        } else{
            Toast.makeText(this, "Lengkapi Input untuk Sign Up", Toast.LENGTH_SHORT).show()
        }
    }
}