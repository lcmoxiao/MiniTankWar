package com.example.minitankwar.gameInfo.gamerole

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.CrashDetector
import com.example.minitankwar.TOOLS.dp30
import com.example.minitankwar.TOOLS.dp40
import com.example.minitankwar.CrashDetector.Companion.crashDetect
import com.example.minitankwar.Object2D
import com.example.minitankwar.TOOLS
import kotlinx.android.synthetic.main.activity_joingame.*

class Tank( val tankId:Int,x: Int, y: Int, direction: Int, val tankBody: View, val tankBarrel: View) :
    Object2D(x, y, direction, dp40, dp30) {
    private var healthy:Int = 10
    private var speed:Int = 10 //移动速度
    private val angularSpeed:Int = 1 //角速度
    private var barrelDiffDirection:Int =0 //炮筒偏移角

    fun updateTankPosition(newDirection: Int): TOOLS.CrashType {
        this.copyPositionData(updatePosition(this, newDirection, speed, angularSpeed))
        shape.updateCenterXY(x,y)
        return crashDetect(this)
    }

    fun moveBack(): TOOLS.CrashType {
        direction += 180
        speed /= 2
        copyPositionData(updateXY(x,y, direction,speed))
        direction -= 180
        speed *= 2
        shape.updateCenterXY(x,y)
        return crashDetect(this)
    }

    fun getBarrelDirection():Int{
        return  (direction + barrelDiffDirection)% 360
    }

    fun updateBarrelDiffDirection(newDirection: Int) {
        barrelDiffDirection = updateDirection(this.getBarrelDirection(), newDirection, angularSpeed) - direction
    }

    fun initInfoAndView(fatherLayout:FrameLayout)
    {
        updateCenterXY()
        setView()
        addViewTo(fatherLayout)
    }

    //设置tank布局位置
    fun setView(){
        TOOLS.setViewPosition(tankBody, x, y)
        TOOLS.setViewPosition(tankBarrel, x, y)
    }

    //更新中心位置
    fun updateCenterXY(){
        shape.updateCenterXY(x,y)
    }

    //更新tank布局到父布局
    fun addViewTo(fatherLayout:FrameLayout)
    {
        fatherLayout.addView(tankBody)
        fatherLayout.addView(tankBarrel)
    }



    fun getCenterX():Int{
        return shape.centerX
    }

    fun getCenterY():Int{
        return shape.centerY
    }

}