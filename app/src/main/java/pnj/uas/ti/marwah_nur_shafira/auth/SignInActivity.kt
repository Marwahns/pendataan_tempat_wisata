package pnj.uas.ti.marwah_nur_shafira.auth

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import pnj.uas.ti.marwah_nur_shafira.R
import pnj.uas.ti.marwah_nur_shafira.content.FragmentActivity
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivitySignInBinding
import pnj.uas.ti.marwah_nur_shafira.sql.DatabaseOpenHelper

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var data: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (binding.txtEmail.text.toString() == "" || binding.txtPass.text.toString() == ""){
            binding.btnSignin.setBackgroundColor(resources.getColor(R.color.login_email_invalid))
            binding.btnSignin.setTextColor(resources.getColor(R.color.white))
            binding.btnSignin.isEnabled = false
        }

        binding.txtEmail.doOnTextChanged { _, _, _, _ ->
            if (Patterns.EMAIL_ADDRESS.matcher(binding.txtEmail.text.toString()).matches()) {
                binding.txtEmail.setBackgroundResource(R.drawable.rounded_border_edit_text)
                binding.btnSignin.setBackgroundColor(resources.getColor(R.color.login))
                binding.btnSignin.setTextColor(resources.getColor(R.color.white))
                binding.btnSignin.isEnabled = true
            } else {
                binding.txtEmail.setBackgroundResource(R.drawable.rounded_warning_border_edit_text)
                binding.btnSignin.setBackgroundColor(resources.getColor(R.color.login_email_invalid))
                binding.btnSignin.setTextColor(resources.getColor(R.color.white))
                binding.btnSignin.isEnabled = false
                binding.txtEmail.error = "Invalid Email Address"
            }
        }

        // Show Hide Password
        binding.showPassword.setOnCheckedChangeListener { _, _ ->
            if (binding.txtPass.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                // If password is visible then Hide it
                binding.txtPass.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                binding.txtPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignin.setOnClickListener {
            val email: String = binding.txtEmail.text.toString()
            val password: String = binding.txtPass.text.toString()

            signinFirebase(email, password)
        }
    }

    private fun signinFirebase(email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    data = getSharedPreferences("dataLogin", MODE_PRIVATE)
                    val edit = data.edit()
                    edit.putString("email", email)
                    edit.apply()
                    finish()
                    val intent = Intent(this, FragmentActivity::class.java)
                    startActivity(intent)
                } else{
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else{
            Toast.makeText(this, "Lengkapi Input untuk Sign In", Toast.LENGTH_SHORT).show()
        }
    }

    // Kalau sudah sign in tidak perlu sign in lagi
    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, FragmentActivity::class.java)
            startActivity(intent)
        }
    }

}