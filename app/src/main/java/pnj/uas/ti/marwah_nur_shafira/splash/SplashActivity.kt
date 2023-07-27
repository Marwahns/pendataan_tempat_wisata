package pnj.uas.ti.marwah_nur_shafira.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import pnj.uas.ti.marwah_nur_shafira.auth.SignInActivity
import pnj.uas.ti.marwah_nur_shafira.databinding.ActivitySplashBinding
import pnj.uas.ti.marwah_nur_shafira.sql.DatabaseOpenHelper

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val databaseHelper = DatabaseOpenHelper(this)
//        databaseHelper.deleteAllData()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}