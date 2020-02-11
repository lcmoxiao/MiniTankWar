package com.example.minitankwar.gameInfo.gamerole

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.TOOLS.dp30
import com.example.minitankwar.TOOLS.dp40
import com.example.minitankwar.CrashDetector.Companion.crashDetect
import com.example.minitankwar.Object2D
import com.example.minitankwar.TOOLS
import java.io.Serializable

class Tank( val tankId:Int,x: Int, y: Int, direction: Int) :
    Object2D(x, y, direction, dp40, dp30),Serializable {

    private var healthy:Int = 10
    private var speed:Int = 10 //移动速度
    private val angularSpeed:Int = 1 //角速度
    private var barrelDiffDirection:Int =0 //炮筒偏移角

    fun updateTankPosition(newDirection: Int,tankBody:View,tankBarrel:View): TOOLS.CrashType {
        this.copyPositionData(updatePosition(this, newDirection, speed, angularSpeed))
        updateCenterXY()
        setView(tankBody,tankBarrel)
        return crashDetect(this)
    }

    fun isDead():Boolean{
        return healthy<=0
    }

    fun hurt(damage:Int){
        healthy -= damage
    }

    fun moveBack(tankBody:View,tankBarrel:View): TOOLS.CrashType {
        direction += 180
        speed /= 2
        copyPositionData(updateXY(x,y, direction,speed))
        direction -= 180
        speed *= 2
        updateCenterXY()
        setView(tankBody,tankBarrel)
        return crashDetect(this)
    }

    fun getBarrelDirection():Int{
        return  (direction + barrelDiffDirection)% 360
    }

    fun updateBarrelDiffDirection(newDirection: Int) {
        barrelDiffDirection = updateDirection(this.getBarrelDirection(), newDirection, angularSpeed) - direction
    }

    fun initInfoAndView(fatherLayout:FrameLayout,tankBody:View,tankBarrel:View)
    {
        updateCenterXY()
        setView(tankBody,tankBarrel)
        addViewTo(fatherLayout,tankBody,tankBarrel)
    }

    //设置tank布局位置
    fun setView(tankBody:View,tankBarrel:View){
        TOOLS.setViewPosition(tankBody, x, y)
        TOOLS.setViewPosition(tankBarrel, x, y)
       tankBody.rotation = -direction.toFloat()
       tankBarrel.rotation = -getBarrelDirection().toFloat()
    }

    //更新tank布局到父布局
    fun addViewTo(fatherLayout:FrameLayout,tankBody:View,tankBarrel:View)
    {
        fatherLayout.addView(tankBody)
        fatherLayout.addView(tankBarrel)
    }

    //remove tank布局从父布局
    fun removeViewFrom(fatherLayout:FrameLayout,tankBody:View,tankBarrel:View)
    {
        fatherLayout.removeView(tankBody)
        fatherLayout.removeView(tankBarrel)
    }

    fun getCenterX():Int{
        return shape.centerX
    }

    fun getCenterY():Int{
        return shape.centerY
    }

}