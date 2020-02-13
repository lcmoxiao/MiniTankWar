package com.example.minitankwar.baseClass

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.TOOLS
import com.example.minitankwar.TOOLS.getMinRotateDir
import com.example.minitankwar.baseinterface.IBaseView2D
import com.example.minitankwar.baseinterface.IShape
import kotlin.math.abs

open class BaseView2D(override val shape: IShape, override val Id: Int) :IBaseView2D {


    //更新子布局参数和位置
    override fun setViewParam(view: View){
        TOOLS.setViewPosition(view,shape.cPoint.x-shape.length/2, shape.cPoint.y-shape.width/2)
        view.rotation = -shape.dir.toFloat()
    }
    //添加子布局到父布局
    override fun addViewTo(fatherLayout: FrameLayout, view:View)
    {
        fatherLayout.addView(view)
    }
    //移除子布局从父布局
    override fun removeViewFrom(fatherLayout: FrameLayout,view:View)
    {
        fatherLayout.removeView(view)
    }
    //会改变布局位置
    override fun updatePosition(newDirection : Double, speed:Double, angular_speed: Double)
    {
        val difference = shape.dir-newDirection
        if(abs(difference)>=angular_speed) {
            shape.rotate(getChangeDirection(shape.dir, newDirection, angular_speed))
        }
        else shape.move(speed)
    }
    override fun getChangeDirection(oldDir:Double, newDirection : Double, angular_speed: Double): Double
    {
        return if(abs(oldDir-newDirection)>=angular_speed) {
            when (getMinRotateDir(oldDir,newDirection)) {
                1 -> -angular_speed
                -1 -> +angular_speed
                else -> 0.0
            }
        }else 0.0
    }
}



