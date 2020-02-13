package com.example.minitankwar.gameInfo.gamerole.buttle

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.CrashDetector.Companion.crashDetect
import com.example.minitankwar.baseClass.BaseView2D
import com.example.minitankwar.TOOLS
import com.example.minitankwar.baseClass.Point
import com.example.minitankwar.baseClass.Shape
import com.example.minitankwar.baseinterface.IBaseView2D
import com.example.minitankwar.gameInfo.gamerole.Tank

abstract class Bullet(length:Double,width:Double,val shotTankId:Int)
    :BaseView2D(Shape(Point(0.0,0.0),0.0,length,width),shotTankId)
{

    abstract val damage:Int
    abstract val speed:Double
    abstract val type:Int
    abstract val loadInterval:Long
    var bulletLiving = true



    //根据tank信息，设置子弹信息
    abstract fun setShotPosition(tank: Tank, diffRotation:Int)

    //执行之后会更新xy坐标，return检测碰撞的结果。
    fun updateBulletPosition(bulletView:View): TOOLS.CrashType
    {
        shape.move(speed)
        setViewParam(bulletView)
        return crashDetect(this)
    }

    fun initInfoAndView(tank: Tank, diffRotation:Int, fatherLayout:FrameLayout, bulletView:View){
        setShotPosition(tank,diffRotation)
        setViewParam(bulletView)
        addViewTo(fatherLayout,bulletView)
    }

}