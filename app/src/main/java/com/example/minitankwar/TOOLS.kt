package com.example.minitankwar

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.gameInfo.gamerole.Tank
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.max
import kotlin.math.min

object TOOLS {

    enum class CrashType{Tank,Wall,Border,NoCrash}
    enum class GunType{Laser,Rocket,Shot}
    //Handler的探测信息标志
    val MOVE_DETECT = 999
    val BARREL_DETECT = 998
    val MOVE_BACK = 997
    val BULLET_SCAN = 996


    val dp5 = dp2px(5)
    val dp10 = dp2px(10)
    val dp13 = dp2px(13)
    val dp15 = dp2px(15)
    val dp30 = dp2px(30)
    val dp40 = dp2px(40)
    val dp410 = dp2px(410)

    private fun dp2px(dpValue: Int): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun getDirectionByTan(x: Int, y: Int, ox: Int, oy: Int): Int {
        val tanX = x - ox
        val tanY = oy - y
        val tan = abs(tanY / tanX.toDouble())
        var direction = Math.toDegrees(atan(tan)).toInt()
        direction = if (tanY > 0) {
            if (tanX < 0) 180 - direction
            else  direction
        } else {
            if (tanX >= 0) 360 - direction
            else 180 + direction
        }
        return direction
    }

    fun setViewPosition(v: View, x:Int, y:Int){
        val params = v.layoutParams as FrameLayout.LayoutParams
        params.setMargins(x, y,0,0)
        v.layoutParams = params
    }



    fun setViewPosition(v: View, x:Int, y:Int, diffX:Int, diffY:Int){
        val params = v.layoutParams as FrameLayout.LayoutParams
        params.setMargins(x+diffX, y+diffY,0,0)
        v.layoutParams = params
    }

    //判断点和线的位置，在线上为0，其余为+ 或 -
    fun dotIsOnLine(dotX:Int,dotY:Int,lx1:Int,ly1:Int,lx2:Int,ly2:Int):Int{
        if(ly1 == ly2) return if(dotY==ly1) 0 else if(dotY>ly1) 1 else -1
        if(lx1 == lx2) return if(dotX==lx1) 0 else if(dotX>lx1) 1 else -1
        val ret = (dotX - lx1)*(lx1-lx2)/(ly1-ly2)+ ly1- dotY
        return when {
            ret == 0 -> 0
            ret>0 -> return 1
            else -> -1
        }
    }

    //判断线和线的位置，两线交叉为true
    fun lineIsOnLine(Lx1:Int, Ly1:Int, Lx2:Int, Ly2:Int, lx1:Int, ly1:Int, lx2:Int, ly2:Int):Boolean{
        val ret1 = dotIsOnLine(Lx1,Ly1,lx1,ly1,lx2,ly2)
        val ret2 = dotIsOnLine(Lx2,Ly2,lx1,ly1,lx2,ly2)
        if(ret1==0||ret2==0)return true
        else if(ret1!=ret2)return true
        val ret3 = dotIsOnLine(lx1,ly1,Lx1,Ly1,Lx2,Ly2)
        val ret4 = dotIsOnLine(lx2,ly2,Lx1,Ly1,Lx2,Ly2)
        if(ret3==0||ret4==0)return true
        else if(ret3!=ret4)return true
        return false
    }

}