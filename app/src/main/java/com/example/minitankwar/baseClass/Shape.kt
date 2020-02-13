package com.example.minitankwar.baseClass

import com.example.minitankwar.TOOLS
import com.example.minitankwar.TOOLS.Loge
import com.example.minitankwar.baseinterface.IPoint
import com.example.minitankwar.baseinterface.IShape
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.math.sqrt

//中心坐标，旋转角度，长和宽
class Shape(override var cPoint: IPoint, override var dir: Double, override var length: Double, override var width: Double) : IShape,Cloneable {

    private val diagonal = (sqrt(((length*length+width*width))) /2)   //除半的对角线长度
    private val tmpTheta = TOOLS.getDirectionByTan(length, width, 0.0, 0.0)

    override fun copyPosAndDir(iShape: IShape) {
        cPoint.copyPos(iShape.cPoint)
        dir = iShape.dir
    }

    override fun copyPosAndDir(x: Double, y: Double, dir: Double) {
        cPoint.copyPos(Point(x,y))
        this.dir = dir
    }

    //这里的ABCD 是无序的四个顶点
    override fun getABCD():ArrayList<IPoint>
    {
        return ArrayList(listOf(getA(),getB(),getC(),getD()))
    }

    //ABCD 分别是 左上，右上，右下，左下
    override fun getOrderABCD():ArrayList<IPoint>{
        return when {
            dir<=90 -> ArrayList(listOf(getB(),getC(),getD(),getA()))
            dir<=180 -> ArrayList(listOf(getA(),getB(),getC(),getD()))
            dir<=270 -> ArrayList(listOf(getD(),getA(),getB(),getC()))
            else -> ArrayList(listOf(getC(),getD(),getA(),getB()))
        }
    }

    //分别是 最左最下最右最上的坐标
    override fun getLimitCoordinates():ArrayList<IPoint>{
        return when {
            dir<=90-tmpTheta -> ArrayList(listOf(getB(),getC(),getD(),getA()))
            dir<=90+tmpTheta -> ArrayList(listOf(getA(),getB(),getC(),getD()))
            dir<=270-tmpTheta -> ArrayList(listOf(getD(),getA(),getB(),getC()))
            else -> ArrayList(listOf(getC(),getD(),getA(),getB()))
        }
    }

    override fun rotate(rotation: Double): IShape {
        dir += rotation
        if(dir>=360)dir-=360
        if(dir<0)dir+=360
        return this
    }

    override fun move(length: Double): IShape {
        cPoint.copyPos(cPoint.getMovePos(dir,length))
        return this
    }


    //获得点位置时，要传入旋转角方向
    private fun getA(): IPoint
    {
        return cPoint.getMovePos(dir + tmpTheta + 0,diagonal)
    }
    private fun getB(): IPoint
    {
        return cPoint.getMovePos(dir - tmpTheta + 180,diagonal)
    }

    private fun getC(): IPoint
    {
        return cPoint.getMovePos(dir + tmpTheta + 180,diagonal)
    }
    private fun getD(): IPoint
    {
        return cPoint.getMovePos(dir - tmpTheta + 360,diagonal)
    }

    override fun toJson(): JSONObject {
        return cPoint.toJson().put("dir", dir)
    }

    override fun copyByJson(js: JSONObject): IShape {
        cPoint.copyByJson(js)
        dir = TOOLS.getDoubleByStringFromJson(js, "dir")
        return this
    }

    override fun clone(): IShape {
        val c = super.clone() as IShape
        c.cPoint = cPoint.clone()
        return c
    }
}