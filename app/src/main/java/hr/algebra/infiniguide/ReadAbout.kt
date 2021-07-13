package hr.algebra.infiniguide

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_read_about.*
import kotlinx.android.synthetic.main.activity_sound_guide.*

class ReadAbout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_about)

        updateUI()

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            updateUI()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }

        thread.start()

    }

    private fun updateUI() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val about = sharedPreference.getString("MonumentAbout", "Monument About")
        val name = sharedPreference.getString("MonumentName", "Monument Name")
        val picturePath = sharedPreference.getString("MonumentPicturePath", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Orlando_Sculpture.jpg/800px-Orlando_Sculpture.jpg")
        tv_read_about_about.text = about
        tv_monument_name.text = name
        Picasso.get()
                .load(picturePath)
                .error(R.drawable.defaultgoogle)
                .transform(RoundedCornersTransformation(80, 8))
                .into(iv_read_picture)

    }

}