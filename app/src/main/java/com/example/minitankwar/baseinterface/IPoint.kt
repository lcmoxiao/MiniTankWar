package com.example.minitankwar.baseinterface

import org.json.JSONObject

interface IPoint {
    var x:Double
    var y:Double

    fun getMovePos(dir:Double, length: Double): IPoint    //计算新方向，返回新位置
    fun copyPos(iPoint: IPoint) //复制坐标
    fun clone(): IPoint

    fun toJson(): JSONObject
    fun copyByJson(js: JSONObject): IPoint
}