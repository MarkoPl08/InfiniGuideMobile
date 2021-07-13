package hr.algebra.infiniguide

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import hr.algebra.infiniguide.api.Api
import hr.algebra.infiniguide.model.Account
import hr.algebra.infiniguide.model.RouteMonument
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener {
            val username = tv_username.text
            val password = tv_password.text

            Api().validLogin(username.toString(), password.toString()).enqueue(object : Callback<Account> {

                override fun onFailure(call: Call<Account>, t: Throwable) {
                    Toast.makeText(applicationContext, "Wrong email or password", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Account>, response: Response<Account>) {
                    if (response.isSuccessful) {
                        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putString("Username", username.toString())
                        editor.putString("Password", password.toString())
                        editor.putString("FullName", response.body()?.FullName)
                        editor.putString("UserID", response.body()?.IDAccount.toString())
                        editor.commit()
                        startActivity<hr.algebra.infiniguide.MainActivity>()
                    } else {
                        Toast.makeText(applicationContext, "Wrong email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            })


        }

    }
}