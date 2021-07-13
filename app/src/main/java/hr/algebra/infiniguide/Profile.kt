package hr.algebra.infiniguide

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import hr.algebra.infiniguide.api.Api
import hr.algebra.infiniguide.model.Account
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userID = sharedPreference.getString("UserID", "UserID")

        btn_change_password.setOnClickListener {

            val oldPassword = sharedPreference.getString("Password", "Password")
            val confirmOldPassword = et_old_password.text.toString()
            val newPassword = et_new_password.text.toString()

            Log.d("Old: ", oldPassword.toString())
            Log.d("New: ", newPassword.toString())

            if (oldPassword != confirmOldPassword) {
                Toast.makeText(applicationContext, "Old password is incorrect!", Toast.LENGTH_SHORT).show()
            } else {
                Api().changePassword(userID!!, newPassword).enqueue(object : Callback<Account> {

                    override fun onFailure(call: Call<Account>, t: Throwable) {
                        Toast.makeText(applicationContext, "Failed to connect", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<Account>, response: Response<Account>) {
                        if (response.isSuccessful) {
                            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                            var editor = sharedPreference.edit()
                            editor.putString("Password", newPassword)
                            editor.commit()
                            Toast.makeText(applicationContext, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Something went wrong.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

        tv_full_name.text = sharedPreference.getString("FullName", "Fullname")
        tv_email.text = sharedPreference.getString("Username", "Email")



    }
}