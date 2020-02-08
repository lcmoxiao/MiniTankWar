package com.example.minitankwar.gameInfo.gamerole

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.TOOLS
import com.example.minitankwar.CrashDetector.Companion.crashDetect
import com.example.minitankwar.Object2D

abstract class Bullet(length:Int,width:Int): Object2D(length,width){
    var loadInterval = 10
    var bulletLiving = true
    abstract val damage:Int
    abstract val speed:Int
    abstract val shotTankId :Int
    abstract val bulletView :View

    //执行之后会更新xy坐标，return检测碰撞的结果。
    fun updateBulletPosition(): TOOLS.CrashType
    {
        this.copyPositionData(updateXY(this,speed))
        shape.updateCenterXY(x,y)
        return crashDetect(this)
    }

    fun setView(){
        TOOLS.setViewPosition(bulletView, x, y)
        bulletView.rotation = -direction.toFloat()
    }

    //更新tank布局到父布局
    fun addViewTo(fatherLayout:FrameLayout)
    {
        fatherLayout.addView(bulletView)
    }

    //根据tank信息，设置子弹信息
    abstract fun setShotPosition(tank: Tank,diffRotation:Int)

    fun initBulletInfoAndView(tank: Tank,diffRotation:Int,fatherLayout:FrameLayout){
        setShotPosition(tank,diffRotation)
        setView()
        addViewTo(fatherLayout)
    }
}