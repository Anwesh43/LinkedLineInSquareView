package com.anwesh.uiprojects.linkedlineinsquarestepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.lineinsquarestepview.LineInSquareView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : LineInSquareView = LineInSquareView.create(this)
    }
}
