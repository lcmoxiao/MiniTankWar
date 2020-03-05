package com.example.minitankwar.baseinterface

import org.json.JSONObject

interface IShape {

    var cPoint: IPoint
    var dir:Double
    var length:Double
    var width:Double

    fun copyPosAndDir(iShape: IShape)    //只复制中心坐标和方向
    fun copyPosAndDir(x:Double,y:Double,dir:Double)    //只复制中心坐标和方向
    fun getABCD():ArrayList<IPoint>    //这里的ABCD 是无序的四个顶点
    fun getOrderABCD():ArrayList<IPoint>    //ABCD 分别是 左上，右上，右下，左下
    fun getLimitCoordinates():ArrayList<IPoint>//分别是 最左最下最右最上的坐标
    fun rotate(rotation:Double): IShape  //旋转
    fun move(length:Double): IShape  //移动

    fun getX():Double

    fun clone(): IShape
    fun toJson(): JSONObject
    fun copyByJson(js: JSONObject): IShape
    fun getY(): Double
}