package hr.algebra.infiniguide

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_splash_screen.*

private const val DELAY: Long = 3000

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        startAnimations()
        redirect()
    }

    private fun startAnimations() {
        ivSplash.applyAnimation(R.anim.blink)
    }

    private fun redirect() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val username = sharedPreference.getString("Username", "User")
        if (username != "User")
            Handler(Looper.getMainLooper()).postDelayed(
                    { startActivity<MainActivity>() },
                    DELAY
            )
        else {
            Handler(Looper.getMainLooper()).postDelayed(
                    { startActivity<Login>() },
                    DELAY
            )
        }

    }
}