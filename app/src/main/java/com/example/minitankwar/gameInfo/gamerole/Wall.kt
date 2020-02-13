package com.example.minitankwar.gameInfo.gamerole

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.baseClass.BaseView2D
import com.example.minitankwar.TOOLS
import com.example.minitankwar.TOOLS.dp40
import com.example.minitankwar.baseClass.Point
import com.example.minitankwar.baseClass.Shape

class Wall(x:Double,y:Double,val view: View): BaseView2D(Shape(Point(x,y),0.0,dp40,dp40),-1){
    private var healthy = 999

    fun initInfoAndView(fatherLayout: FrameLayout)
    {
        setViewParam(view)
        addViewTo(fatherLayout)
    }


    //更新tank布局到父布局
    private fun addViewTo(fatherLayout:FrameLayout)
    {
        fatherLayout.addView(view)
    }

}