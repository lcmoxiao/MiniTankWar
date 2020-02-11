package com.example.minitankwar.gameInfo.gamerole

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.CrashDetector.Companion.crashDetect
import com.example.minitankwar.Object2D
import com.example.minitankwar.TOOLS

abstract class Bullet(length:Int,width:Int): Object2D(length,width){
    var loadInterval = 10
    var bulletLiving = true
    abstract val damage:Int
    abstract val speed:Int
    abstract val shotTankId :Int
    abstract val type:Int




    //执行之后会更新xy坐标，return检测碰撞的结果。
    fun updateBulletPosition(bulletView:View): TOOLS.CrashType
    {
        this.copyPositionData(updateXY(this,speed))
        updateCenterXY()
        setView(bulletView)
        return crashDetect(this)
    }



    //根据tank信息，设置子弹信息
    abstract fun setShotPosition(tank: Tank,diffRotation:Int)

    fun initInfoAndView(tank: Tank, diffRotation:Int, fatherLayout:FrameLayout,bulletView:View){
        setShotPosition(tank,diffRotation)
        setView(bulletView)
        addViewTo(fatherLayout,bulletView)
    }


}