package com.example.minitankwar

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import org.json.JSONObject
import java.lang.Integer.parseInt
import java.net.*
import java.util.*
import kotlin.math.abs
import kotlin.math.atan

object TOOLS {

    enum class CrashType{Tank,Wall,Border,NoCrash}
    enum class GunType{Laser,Rocket,Shot}

    var tmpTankID:Int = 0
    var gameMode:Int = 0   //0单人 1 双人

    val dp5 = dp(5)
    val dp10 = dp(10)
    val dp13 = dp(13)
    val dp30 = dp(30)
    val dp40 = dp(40)
    val dp250 = dp(250)
    val dp410 = dp(410)


    fun Loge(xx:String)
    {
        Log.e("123",""+xx)
    }

    fun Loge(x:Int,xx:String)
    {
        Log.e("123", "$x $xx")
    }


    fun dp(dpValue: Int): Double {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toDouble()
    }

    fun getDirectionByTan(x: Double, y: Double, ox: Double, oy: Double): Double {
        val tanX = x - ox
        val tanY = oy - y
        val tan = abs(tanY / tanX)
        var direction = Math.toDegrees(atan(tan))
        direction = if (tanY > 0) {
            if (tanX < 0) 180 - direction
            else  direction
        } else {
            if (tanX >= 0) 360 - direction
            else 180 + direction
        }
        return direction
    }

    fun setViewPosition(v: View, x:Double, y:Double){
        val params = v.layoutParams as FrameLayout.LayoutParams
        params.setMargins(x.toInt(), y.toInt(),0,0)
        v.layoutParams = params
    }

    // 根据Int获取子弹类型
    fun getBulletType(type:Int):GunType
    {
        return when(type){
            1-> GunType.Shot
            2-> GunType.Rocket
            3-> GunType.Laser
            else-> GunType.Shot
        }
    }

    // 根据Int获取子弹类型
    fun getBulletTypeInt(type:GunType):Int
    {
        return when(type){
            GunType.Shot-> 1
            GunType.Rocket-> 2
            GunType.Laser-> 3
        }
    }


    //从Json中用String获取Double
    fun getDoubleByStringFromJson(jsonObject: JSONObject, string: String):Double
    {
        return jsonObject.getString(string).toDouble()
    }
    //从Json中用String获取Int
    fun getIntByStringFromJson(jsonObject: JSONObject, string: String):Int
    {
        return parseInt(jsonObject.getString(string))
    }

    //判断点和线的位置，在线上为0，其余为+ 或 -
    private fun dotIsOnLine(dotX:Double,dotY:Double,lx1:Double,ly1:Double,lx2:Double,ly2:Double):Int{
        if(ly1 == ly2) return if(dotY==ly1) 0 else if(dotY>ly1) 1 else -1
        if(lx1 == lx2) return if(dotX==lx1) 0 else if(dotX>lx1) 1 else -1
        val ret = (dotX - lx1)*(lx1-lx2)/(ly1-ly2)+ ly1- dotY
        return when {
            ret == 0.0 -> 0
            ret>0 -> return 1
            else -> -1
        }
    }

    //判断线和线的位置，两线交叉为true
    fun lineIsOnLine(Lx1:Double, Ly1:Double, Lx2:Double, Ly2:Double, lx1:Double, ly1:Double, lx2:Double, ly2:Double):Boolean{
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

    fun getIpAddressString(): String? {
        try {
            val enNetI: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (enNetI.hasMoreElements()) {
                val netI: NetworkInterface = enNetI.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = netI.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return "0.0.0.0"
    }

    //寻找需要旋转角度最小的旋转方向，顺时针为1，逆时针为-1,相等为0
    fun getMinRotateDir(_oldDirection:Double,newDirection : Double):Int {
        val difference = _oldDirection - newDirection
        if (abs(difference) == 0.0) return 0
        return when {
            difference > 180 -> -1
            difference > 0 -> 1
            difference > -180 -> -1
            difference > -360 -> 1
            else -> 1
        }
    }



}