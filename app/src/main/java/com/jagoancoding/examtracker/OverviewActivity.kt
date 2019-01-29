package com.jagoancoding.examtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jagoancoding.examtracker.ui.overview.OverviewFragment

class OverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.overview_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, OverviewFragment.newInstance())
                .commitNow()
        }
    }

}
