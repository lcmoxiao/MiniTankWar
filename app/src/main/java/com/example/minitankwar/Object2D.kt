package com.example.minitankwar

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.TOOLS.getIntByStringFromJson
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

//包括坐标和方向
open class Object2D:Cloneable{
    var x:Int
    var y:Int
    var direction:Int
    var viewType:Int
    val shape:Shape



    constructor() {
        this.x =0
        this.y= 0
        this.direction = 0
        viewType=0
        this.shape = Shape(0,0)
    }

    constructor(length: Int,width:Int){
        this.x =0
        this.y= 0
        this.direction = 0
        viewType=0
        this.shape = Shape(length,width)
    }

    constructor(x: Int,y: Int,direction:Int,type:Int){
        this.x =x
        this.y= y
        this.direction = direction
        viewType=type
        this.shape = Shape(0,0)
    }

    constructor(x: Int,y: Int,direction: Int,length: Int,width:Int){
        this.x =x
        this.y= y
        this.direction = direction
        viewType=0
        this.shape = Shape(length,width)
    }

    fun copyPositionData(object2D: Object2D){
        this.x = object2D.x
        this.y = object2D.y
        this.direction = object2D.direction
    }

    fun copyPositionData(x: Int,y:Int,direction: Int){
        this.x = x
        this.y = y
        this.direction = direction
    }

    public override fun clone(): Object2D {
        return super.clone() as Object2D
    }

    fun toJson():JSONObject{
        return JSONObject().put("x",x).put("y", y).put("dir", direction)
    }

    fun copyByJsonFromByteArray(listenBuf:ByteArray){
        val js = JSONObject(String(listenBuf,0,listenBuf.size))
        x = getIntByStringFromJson(js,"x")
        y = getIntByStringFromJson(js,"y")
        direction = getIntByStringFromJson(js,"dir")
        updateCenterXY()
    }

    fun logPosition(){
        Log.e("position", "x $x y $y")
    }

    //更新中心位置
    fun updateCenterXY(){
        shape.updateCenterXY(x,y)
    }

    fun setView(view: View){
        TOOLS.setViewPosition(view, x, y)
        view.rotation = -direction.toFloat()
    }

    //更新ta布局到父布局
    fun addViewTo(fatherLayout: FrameLayout, bulletView:View)
    {
        fatherLayout.addView(bulletView)
    }

    //通过相对坐标变换，通过tan值来获得相对的角度，以北方为0度来计算
    companion object{
        //——返回新的position
        fun updatePosition(object2D: Object2D, newDirection : Int, speed:Int, angular_speed: Int): Object2D
        {
            val oldPosition = object2D.clone()
            val difference = oldPosition.direction-newDirection
            return if(abs(difference)>=angular_speed) {
                oldPosition.direction=updateDirection(oldPosition.direction, newDirection, angular_speed)
                oldPosition
            }else {
                oldPosition.copyPositionData(
                    updateXY(
                        oldPosition,
                        speed
                    )
                )
                oldPosition
            }
        }

        //寻找最小的旋转角，并旋转，-返回旋转后的角度
        fun updateDirection(_oldDirection:Int,newDirection : Int,angular_speed:Int):Int{
            var oldDirection = _oldDirection
            val difference = _oldDirection-newDirection
            if(abs(difference)>=angular_speed)
            {
                when {
                    difference>180 ->oldDirection+=angular_speed
                    difference>0 ->oldDirection-=angular_speed
                    difference>-180 ->oldDirection+=angular_speed
                    difference>-360 -> oldDirection-=angular_speed
                }
                if (oldDirection<0) {
                    oldDirection += 360
                }
                else if(oldDirection>359) {
                    oldDirection -= 360
                }
            }
            return oldDirection
        }

        //计算新方向，返回位置
        fun updateXY(object2D: Object2D, speed:Int): Object2D {
            val oldPosition = object2D.clone()
            oldPosition.y -= (speed * sin(Math.toRadians(object2D.direction.toDouble()))).toInt()
            oldPosition.x += (speed * cos(Math.toRadians(object2D.direction.toDouble()))).toInt()
            return oldPosition
        }

        //计算新方向，返回位置
        fun updateXY(x:Int,y:Int,direction:Int,length:Int): Object2D {
            val oldPosition = Object2D()
            oldPosition.direction = direction
            oldPosition.y = y-(length * sin(Math.toRadians(direction.toDouble()))).toInt()
            oldPosition.x = x+(length * cos(Math.toRadians(direction.toDouble()))).toInt()
            return oldPosition
        }
    }
}