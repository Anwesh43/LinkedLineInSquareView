package com.anwesh.uiprojects.lineinsquarestepview

/**
 * Created by anweshmishra on 27/11/18.
 */

import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.view.View
import android.view.MotionEvent

val nodes : Int = 5
val lines : Int = 4
val scDiv : Double = 0.51
val scGap : Float = 0.05f
val color : Int = Color.parseColor("#283593")
val sizeFactor : Float = 2.5f
val strokeFactor : Int = 120
val DELAY : Long = 25

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
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = color
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    save()
    translate(gap * (i + 1), h/2)
    rotate(90f * sc2)
    drawRect(RectF(-size, -size, size, size), paint)
    for (j in 0..(lines-1)) {
        val sc : Float = sc1.divideScale(j, lines)
        val hGap : Float = (2 * size) / (lines + 1)
        save()
        translate(0f, -size + hGap * (j + 1))
        val ws : Float = size * 0.5f * sc
        drawLine(-ws, 0f, ws, 0f, paint)
        restore()
    }
    restore()
}

class LineInSquareView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateScale(dir, lines, 1)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(DELAY)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class LISNode(var i : Int, val state : State = State()) {
        private var next : LISNode? = null
        private var prev : LISNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = LISNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawLISNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : LISNode {
            var curr : LISNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LineInSquare(var i : Int) {

        private val root : LISNode = LISNode(0)

        private var curr : LISNode = root

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : LineInSquareView) {

        private val animator : Animator = Animator(view)

        private val lis : LineInSquare = LineInSquare(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            lis.draw(canvas, paint)
            animator.animate {
                lis.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lis.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity: Activity) : LineInSquareView {
            val view : LineInSquareView = LineInSquareView(activity)
            activity.setContentView(view)
            return view
        }
    }
}