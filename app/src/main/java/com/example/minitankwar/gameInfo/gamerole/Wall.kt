package com.example.minitankwar.gameInfo.gamerole

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.Object2D
import com.example.minitankwar.Shape
import com.example.minitankwar.TOOLS
import com.example.minitankwar.TOOLS.dp40

class Wall(x:Int,y:Int,val view: View): Object2D(x,y,0,dp40,dp40){
    private var healthy = 999

    fun initInfoAndView(fatherLayout: FrameLayout)
    {
        updateCenterXY()
        setView()
        addViewTo(fatherLayout)
    }

    //设置tank布局位置
    fun setView(){
        TOOLS.setViewPosition(view, x, y)
    }

    //更新tank布局到父布局
    fun addViewTo(fatherLayout:FrameLayout)
    {
        fatherLayout.addView(view)
    }

}