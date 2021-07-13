package hr.algebra.infiniguide

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import hr.algebra.infiniguide.api.Api
import hr.algebra.infiniguide.model.RouteMonument
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_sound_guide.*
import kotlinx.android.synthetic.main.route.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SoundGuide : AppCompatActivity() {


    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    private var pause: Boolean = false

    lateinit var routeMonuments: List<RouteMonument>
    private var routeSize: Int = 3
    private var currentPossition: Int = 1

    private val sounds = mapOf(
            "1800000" to R.raw.franjevacki_samostan,
            "1800001" to R.raw.knezev_dvor,
            "1800002" to R.raw.ulica_siroka,
            "1800003" to R.raw.gunduliceva_poljana,
            "1800004" to R.raw.dominikanski_samostan,
            "1800005" to R.raw.dom_marina_drzica,
            "1800007" to R.raw.onofrijeva_fontana,
            "1800008" to R.raw.minceta,
            "1800009" to R.raw.porporela,
            "1800010" to R.raw.vrata_od_buze,
            "1800011" to R.raw.tvrdava_bokar,
            "1800012" to R.raw.orlandov_stup,
            "1800013" to R.raw.stradun,
            "1800014" to R.raw.vrata_od_pila,
            "1800015" to R.raw.zvonik)

    private var currentSound: String = "1800001"
    private var lastSound: String = "1800000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_guide)

        playBtn.setOnClickListener {
            if (pause) {
                mediaPlayer.seekTo(mediaPlayer.currentPosition)
                mediaPlayer.start()
                pause = false
                //Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()
            } else {
                mediaPlayer = MediaPlayer.create(applicationContext, sounds[lastSound]!!)
                mediaPlayer.start()
                //Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()

            }
            initializeSeekBar()
            playBtn.isEnabled = false
            pauseBtn.isEnabled = true
            stopBtn.isEnabled = true

            mediaPlayer.setOnCompletionListener {
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                //Toast.makeText(this, "end", Toast.LENGTH_SHORT).show()
            }
        }
        // Pause the media player
        pauseBtn.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                pause = true
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = true
                //Toast.makeText(this, "media pause", Toast.LENGTH_SHORT).show()
            }
        }
        // Stop the media player
        stopBtn.setOnClickListener {
            if (mediaPlayer.isPlaying || pause.equals(true)) {
                pause = false
                seek_bar.setProgress(0)
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                handler.removeCallbacks(runnable)

                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                tv_pass.text = ""
                tv_due.text = ""
                //Toast.makeText(this, "media stop", Toast.LENGTH_SHORT).show()
            }
        }
        // Seek bar change listener
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
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
        setVariables()
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val name = sharedPreference.getString("MonumentPicturePath", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Orlando_Sculpture.jpg/800px-Orlando_Sculpture.jpg")
        Picasso.get()
                .load(name)
                .error(R.drawable.defaultgoogle)
                .transform(RoundedCornersTransformation(80, 8))
                .into(iv_sound_picture)
    }

    private fun setVariables() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val name = sharedPreference.getString("MonumentName", "Monument Name")
        currentSound = sharedPreference.getString("MonumentSound", "1800004")!!
        if (currentSound != lastSound) {
            mediaPlayer = MediaPlayer.create(applicationContext, sounds[currentSound]!!)
            lastSound = currentSound
        }
        tv_sound_monument_name.text = name
    }

    private fun initializeSeekBar() {
        seek_bar.max = mediaPlayer.seconds

        runnable = Runnable {
            seek_bar.progress = mediaPlayer.currentSeconds

            tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    val MediaPlayer.seconds: Int
        get() {
            return this.duration / 1000
        }

    val MediaPlayer.currentSeconds: Int
        get() {
            return this.currentPosition / 1000
        }
}




