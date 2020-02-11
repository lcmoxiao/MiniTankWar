package com.example.minitankwar

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import org.json.JSONObject
import java.io.*
import java.net.*
import java.util.*
import kotlin.math.abs
import kotlin.math.atan

object TOOLS {

    enum class CrashType{Tank,Wall,Border,NoCrash}
    enum class GunType{Laser,Rocket,Shot}

    var tmpTankID:Int = 0
    var gameMode:Int = 0   //0单人 1 双人
    var listenSocket  = DatagramSocket(12306)
    lateinit var listenPacket :DatagramPacket
    val sendSocket = DatagramSocket(12307)
    lateinit var sendPacket :DatagramPacket

    val dp5 = dp(5)
    val dp10 = dp(10)
    val dp13 = dp(13)
    val dp15 = dp(15)
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
        Log.e("123",x.toString()+" "+xx)
    }


    fun dp(dpValue: Int): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun getDirectionByTan(x: Int, y: Int, ox: Int, oy: Int): Int {
        val tanX = x - ox
        val tanY = oy - y
        val tan = abs(tanY / tanX.toDouble())
        var direction = Math.toDegrees(atan(tan)).toInt()
        direction = if (tanY > 0) {
            if (tanX < 0) 180 - direction
            else  direction
        } else {
            if (tanX >= 0) 360 - direction
            else 180 + direction
        }
        return direction
    }

    fun setViewPosition(v: View, x:Int, y:Int){
        val params = v.layoutParams as FrameLayout.LayoutParams
        params.setMargins(x, y,0,0)
        v.layoutParams = params
    }

    // 根据Int获取子弹类型
    fun getBulletType(type:Int):TOOLS.GunType
    {
        when(type){
            1->return TOOLS.GunType.Shot
            2->return TOOLS.GunType.Rocket
            3->return TOOLS.GunType.Laser
            else-> return TOOLS.GunType.Shot
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


    //从Json中用String获取int
    fun getIntByStringFromJson(jsonObject: JSONObject, string: String):Int
    {
        return Integer.parseInt(jsonObject.getString(string))
    }


    fun setViewPosition(v: View, x:Int, y:Int, diffX:Int, diffY:Int){
        val params = v.layoutParams as FrameLayout.LayoutParams
        params.setMargins(x+diffX, y+diffY,0,0)
        v.layoutParams = params
    }

    //判断点和线的位置，在线上为0，其余为+ 或 -
    fun dotIsOnLine(dotX:Int,dotY:Int,lx1:Int,ly1:Int,lx2:Int,ly2:Int):Int{
        if(ly1 == ly2) return if(dotY==ly1) 0 else if(dotY>ly1) 1 else -1
        if(lx1 == lx2) return if(dotX==lx1) 0 else if(dotX>lx1) 1 else -1
        val ret = (dotX - lx1)*(lx1-lx2)/(ly1-ly2)+ ly1- dotY
        return when {
            ret == 0 -> 0
            ret>0 -> return 1
            else -> -1
        }
    }

    //判断线和线的位置，两线交叉为true
    fun lineIsOnLine(Lx1:Int, Ly1:Int, Lx2:Int, Ly2:Int, lx1:Int, ly1:Int, lx2:Int, ly2:Int):Boolean{
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

    //1.发送的字符，2.发送使用的DatagramSocket
    fun send(str: String, socket: DatagramSocket, address:InetAddress, port:Int){
        val bytes1 = str.toByteArray()
        val p = DatagramPacket(bytes1, bytes1.size, address, port)
        socket.send(p)
    }

    //1.发送的字符，2.发送使用的DatagramSocket
    fun send(bytes1: ByteArray, socket: DatagramSocket, address:InetAddress, port:Int){
        val p = DatagramPacket(bytes1, bytes1.size, address, port)
        socket.send(p)
    }

    //1.接受使用的字符，2.接受使用的字符串
    fun recv(socket: DatagramSocket, receiveBuf: ByteArray): DatagramPacket {
        val receiverPacket = DatagramPacket(receiveBuf, receiveBuf.size)
        socket.receive(receiverPacket)
        return receiverPacket
    }

    fun objectToByteArray(obj:Any):ByteArray {
        val bytes :ByteArray
        val objectOutputStream:ObjectOutputStream
        val byteArrayOutputStream = ByteArrayOutputStream()
        objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        objectOutputStream.flush()
        bytes = byteArrayOutputStream.toByteArray()
        return bytes
    }

    fun byteArrayToObject(bytes :ByteArray):Any {
        val obj:Any
        val objectInputStream: ObjectInputStream
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        objectInputStream = ObjectInputStream(byteArrayInputStream)
        obj = objectInputStream.readObject()
        return obj
    }


}