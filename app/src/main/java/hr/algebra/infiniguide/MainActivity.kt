package hr.algebra.infiniguide


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.algebra.infiniguide.api.Api
import hr.algebra.infiniguide.model.Route
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation = findViewById<View>(R.id.bottomNav) as BottomNavigationView
        val menu = navigation.menu
        val menuItem: MenuItem = menu.getItem(0)
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


        Api().getRoutes().enqueue(object : Callback<List<Route>> {
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

        val imageViewInfo = findViewById<ImageView>(R.id.info)
        imageViewInfo.setOnClickListener{
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

    }
            private fun showRoutes(routes: List<Route>) {
                rvItems.layoutManager = LinearLayoutManager(this@MainActivity)
                rvItems.adapter = ItemAdapter(routes)
            }
}