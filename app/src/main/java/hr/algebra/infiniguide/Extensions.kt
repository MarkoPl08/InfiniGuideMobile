package hr.algebra.infiniguide

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.AnimationUtils

fun View.applyAnimation(resourceId: Int) = startAnimation(AnimationUtils.loadAnimation(context,resourceId))

inline fun <reified T : Activity> Context.startActivity() = Intent(this, T::class.java).apply{
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(this)
}