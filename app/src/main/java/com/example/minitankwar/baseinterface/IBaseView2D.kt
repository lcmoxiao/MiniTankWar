package com.example.minitankwar.baseinterface

import android.view.View
import android.widget.FrameLayout

interface IBaseView2D {

    val shape: IShape
    val Id :Int
    fun setViewParam(view: View)
    fun addViewTo(fatherLayout: FrameLayout, view: View)
    fun removeViewFrom(fatherLayout: FrameLayout, view: View)
    fun updatePosition(newDirection: Double, speed: Double, angular_speed: Double)
    fun getChangeDirection(oldDir: Double, newDirection: Double, angular_speed: Double): Double
}