package com.example.minitankwar

import kotlin.collections.ArrayList
import kotlin.math.sqrt


//坦克和子弹以及其他东西的形状，有中心位置，和半长对角线，动态位置要使用update实现中心点位置更新，
class Shape(
    private val length: Int,
    width: Int
) {
    var centerX = 0//矩形中心x坐标
    var centerY = 0//矩形中心y坐标
    private val diagonal = (sqrt(((length*length+width*width).toDouble())) /2).toInt()   //除半的
    private val tmpTheta = TOOLS.getDirectionByTan(length, width, 0, 0)

    fun updateCenterXY(x:Int,y:Int){
        centerX = x+length/2
        centerY = y+length/2
    }

    //这里的ABCD 是无序的四个顶点
    fun getABCD(direction:Int):ArrayList<Object2D>
    {
        return ArrayList(listOf(getA(direction),getB(direction),getC(direction),getD(direction)))
    }

    //ABCD 分别是 左上，右上，右下，左下
    fun getOrderABCD(direction: Int):ArrayList<Object2D>{
        return when {
            direction<=90 -> ArrayList(listOf(getB(direction),getC(direction),getD(direction),getA(direction)))
            direction<=180 -> ArrayList(listOf(getA(direction),getB(direction),getC(direction),getD(direction)))
            direction<=270 -> ArrayList(listOf(getD(direction),getA(direction),getB(direction),getC(direction)))
            else -> ArrayList(listOf(getC(direction),getD(direction),getA(direction),getB(direction)))
        }
    }

    //分别是 最左最下最右最上的坐标
    fun getLimitCoordinates(direction: Int):ArrayList<Object2D>{
        return when {
            direction<=90-tmpTheta -> ArrayList(listOf(getB(direction),getC(direction),getD(direction),getA(direction)))
            direction<=90+tmpTheta -> ArrayList(listOf(getA(direction),getB(direction),getC(direction),getD(direction)))
            direction<=270-tmpTheta -> ArrayList(listOf(getD(direction),getA(direction),getB(direction),getC(direction)))
            else -> ArrayList(listOf(getC(direction),getD(direction),getA(direction),getB(direction)))
        }
    }

    //获得点位置时，要传入旋转角方向
    private fun getA(direction:Int): Object2D
    {
        return Object2D.updateXY(
            centerX,
            centerY,
            direction + tmpTheta + 0,
            diagonal
        )
    }

    private fun getB(direction:Int): Object2D
    {
        return Object2D.updateXY(
            centerX,
            centerY,
            direction - tmpTheta + 180,
            diagonal
        )
    }
    private fun getC(direction:Int): Object2D
    {
        return Object2D.updateXY(
            centerX,
            centerY,
            direction + tmpTheta + 180,
            diagonal
        )
    }
    private fun getD(direction:Int): Object2D
    {
        return Object2D.updateXY(
            centerX,
            centerY,
            direction - tmpTheta + 360,
            diagonal
        )
    }




}