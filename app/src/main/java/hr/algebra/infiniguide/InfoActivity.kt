package hr.algebra.infiniguide


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        val aboutPage: View = AboutPage(this)
            .isRTL(false)
            .addItem(Element().setTitle("Version 1.0.0"))
            .setDescription("\n" +
                    "InfiniGuide je ideja koja je nastala u doba korona krize. Cilj projekta je olakšat ljudima planiranje i posjećivanje mjesta koja su oduvijek htjeli posjetiti ali su se bojali velikih gužvi tijekom razgledavanja grada.")
            .addGroup("Connect with us")
            .addEmail("infiniguide@gmail.com")
            .addWebsite("https://infiniguide.com/")
            .addFacebook("InfiniGuide")
            .addTwitter("InfiniGuide")
            .addInstagram("InfiniGuide")
            .addGitHub("InfiniGuide")
            .create()

        setContentView(aboutPage)
    }
}