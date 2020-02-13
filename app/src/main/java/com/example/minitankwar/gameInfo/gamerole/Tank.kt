package com.example.minitankwar.gameInfo.gamerole

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.TOOLS.dp30
import com.example.minitankwar.TOOLS.dp40
import com.example.minitankwar.CrashDetector.Companion.crashDetect
import com.example.minitankwar.baseClass.BaseView2D
import com.example.minitankwar.TOOLS
import com.example.minitankwar.baseClass.Point
import com.example.minitankwar.baseClass.Shape

class Tank( val tankId:Int,x: Double, y: Double, direction: Double):BaseView2D(Shape(Point(x, y), direction, dp40, dp30),tankId)
{
    private var healthy:Double = 10.0
    private var speed:Double = 10.0 //移动速度
    private val angularSpeed:Double = 1.0 //角速度
    private var barrelDiffDirection:Double =0.0 //炮筒偏移角

    fun updateTankPosition(newDirection: Double,tankBody:View,tankBarrel:View): TOOLS.CrashType {
        updatePosition( newDirection, speed, angularSpeed)
        setView(tankBody,tankBarrel)
        return crashDetect(this)
    }

    fun moveBack(tankBody:View,tankBarrel:View): TOOLS.CrashType {
        speed /= 2
        shape.move(-speed)
        speed *= 2
        setView(tankBody,tankBarrel)
        return crashDetect(this)
    }

    fun isDead():Boolean{
        return healthy<=0
    }

    fun hurt(damage:Int){
        healthy -= damage
    }

    fun getBarrelDirection():Double{
        return  (shape.dir + barrelDiffDirection)% 360
    }

    fun updateBarrelDiffDirection(newDirection: Double) {
        barrelDiffDirection +=getChangeDirection(getBarrelDirection(), newDirection, angularSpeed)
    }

    fun initShapeAndViewParam(fatherLayout:FrameLayout,tankBody:View,tankBarrel:View)
    {
        setView(tankBody,tankBarrel)
        addViewTo(fatherLayout,tankBody)
        addViewTo(fatherLayout,tankBarrel)
    }

    //设置tank布局位置
    fun setView(tankBody:View,tankBarrel:View){
        setViewParam(tankBody)
        setViewParam(tankBarrel)
        tankBody.rotation = (-shape.dir).toFloat()
        tankBarrel.rotation = -getBarrelDirection().toFloat()
    }

    //remove tank布局从父布局
    fun removeViewFrom(fatherLayout:FrameLayout,tankBody:View,tankBarrel:View)
    {
        fatherLayout.removeView(tankBody)
        fatherLayout.removeView(tankBarrel)
    }


}