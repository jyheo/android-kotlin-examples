package com.example.fragment2

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity(), TitlesFragment.OnTitleSelectedListener {

    private var mbDetailsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mbDetailsVisible = findViewById<View>(R.id.details) != null
    }

    override fun onTitleSelected(i: Int, restoreSaved: Boolean) {
        if (mbDetailsVisible) {
            val detailsFragment = DetailsFragment.newInstance(i)
            supportFragmentManager.beginTransaction().replace(R.id.details, detailsFragment).commit()
        } else {
            if (!restoreSaved) {
                val intent = Intent(this, DetailsActivity::class.java)
                intent.putExtra("index", i)
                startActivity(intent)
            }
        }
    }
}
