package com.anwesh.uiprojects.lineinsquarestepview

/**
 * Created by anweshmishra on 27/11/18.
 */

import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.MotionEvent

val nodes : Int = 5
val lines : Int = 4
val scDiv : Double = 0.51
val scGap : Float = 0.05f
val color : Int = Color.parseColor("#283593")
val sizeFactor : Float = 2.5f
val strokeFactor : Int = 120
fun Int.getInverse()  : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), Math.max(0f, this - i * n.getInverse())) * n

fun Float.valueFactor() : Float = Math.floor(this / scDiv).toFloat()

fun Float.mirrorValue(a : Int, b : Int) : Float = (1f - valueFactor()) * a.getInverse() + valueFactor() * b.getInverse()

fun Float.updateScale(dir : Float, a : Int, b : Int) : Float = scGap * dir * mirrorValue(a, b)

fun Canvas.drawLISNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    save()
    translate(gap * (i + 1), h/2)
    rotate(90f * sc2)
    for (j in 0..(lines-1)) {
        val sc : Float = sc1.divideScale(j, lines)
        val hGap : Float = (2 * size) / (lines + 1)
        save()
        translate(0f, -size/2 + hGap * (j + 1))
        val ws : Float = size * 0.5f
        drawLine(-ws, 0f, ws, 0f, paint)
        restore()
    }
    restore()
}