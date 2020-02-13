package com.example.minitankwar.baseClass

import com.example.minitankwar.TOOLS
import com.example.minitankwar.baseinterface.IPoint
import org.json.JSONObject
import kotlin.math.cos
import kotlin.math.sin

class Point(override var x: Double, override var y: Double):
    IPoint,Cloneable {

    //计算新方向，返回位置，原位置不变
    override fun getMovePos(dir:Double, length: Double): IPoint {
        val point = this.clone()
        point.y -= (length * sin(Math.toRadians(dir)))
        point.x += (length * cos(Math.toRadians(dir)))
        return point
    }

    override fun copyPos(iPoint: IPoint) {
        this.x = iPoint.x
        this.y = iPoint.y
    }

    override fun clone(): IPoint {
        return super.clone() as IPoint
    }

    override fun toJson(): JSONObject {
        return JSONObject().put("x",x).put("y", y)
    }

    override fun copyByJson(js: JSONObject): IPoint {
        x = TOOLS.getDoubleByStringFromJson(js, "x")
        y = TOOLS.getDoubleByStringFromJson(js, "y")
        return this
    }

}