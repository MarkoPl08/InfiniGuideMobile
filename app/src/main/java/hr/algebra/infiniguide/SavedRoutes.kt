package hr.algebra.infiniguide

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.algebra.infiniguide.api.Api
import hr.algebra.infiniguide.model.Route
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedRoutes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_routes)

        val navigation = findViewById<View>(R.id.bottomNav) as BottomNavigationView
        val menu = navigation.menu
        val menuItem: MenuItem = menu.getItem(2)
        menuItem.isChecked = true
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.myLocation -> {
                    val b = Intent(this, MainActivity::class.java)
                    startActivity(b)
                }
                R.id.myRoutes -> {
                    val b = Intent(this, MineRoutes::class.java)
                    startActivity(b)
                }
                R.id.savedRoutes -> {
                    val b = Intent(this, SavedRoutes::class.java)
                    startActivity(b)
                }
                R.id.googleMap -> {
                    val b = Intent(this, MapsActivity::class.java)
                    startActivity(b)
                }
            }
            false
        }
        val imageView = findViewById<ImageView>(R.id.profile)
        imageView.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        val imageView1 = findViewById<ImageView>(R.id.goBackButton)
        imageView1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val id = sharedPreference.getString("UserID", "23")

        Api().getSavedRoutes(id.toString()).enqueue(object : Callback<List<Route>> {
            override fun onFailure(call: Call<List<Route>>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Route>>, response: Response<List<Route>>) {
                val movies = response.body()
                movies?.let {
                    showRoutes(it)
                }
            }
        })
    }
    private fun showRoutes(routes: List<Route>) {
        rvItems.layoutManager = LinearLayoutManager(this@SavedRoutes)
        rvItems.adapter = ItemAdapter(routes)
    }
}