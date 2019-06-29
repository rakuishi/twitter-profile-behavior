package com.rakuishi.behavior

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val behavior = ProfileBehavior(findViewById(R.id.rootView))
        behavior.onRefresh = {
            GlobalScope.launch(Dispatchers.Main) {
                delay(1000L)
                behavior.reset()
            }
        }
    }
}