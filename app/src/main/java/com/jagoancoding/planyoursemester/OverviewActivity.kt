/*
 * Copyright 2019 Maharaj Fawwaz Almuqaddim Yusran
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jagoancoding.planyoursemester

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jagoancoding.planyoursemester.ui.addnewplan.AddPlanFragment

class OverviewActivity : AppCompatActivity(),
    AddPlanFragment.OnFragmentInteractionListener {

    private var navController: NavController? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.overview_activity)

        // Set up app toolbar
        toolbar = findViewById(R.id.overview_toolbar)

        navController = findNavController(R.id.nav_host_fragment)
        if (navController != null) {
            val appBarConfiguration = AppBarConfiguration(navController!!.graph)
            toolbar?.setupWithNavController(
                navController!!,
                appBarConfiguration
            )
        }
    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
