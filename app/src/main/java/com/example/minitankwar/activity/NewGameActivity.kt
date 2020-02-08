package com.example.minitankwar.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.minitankwar.R
import kotlinx.android.synthetic.main.activity_newgame.*
import java.io.IOException
import java.net.*
import java.net.NetworkInterface.getNetworkInterfaces
import java.util.*


class NewGameActivity : AppCompatActivity() {

    var listening = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)
        initclick()
        ipnowtext.text = "本地IP:"+getIpAddressString()
    }

    fun getIpAddressString(): String? {
        try {
            val enNetI: Enumeration<NetworkInterface> = getNetworkInterfaces()
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


    fun initclick()
    {
        iploadtv.setOnClickListener {
            if(!listening)
            {iploadtv.text = "正在监听"
                listening=true}
            else {
                iploadtv.text = "点击开启监听"
                udp("127.0.0.1")
                listening=false
            }

            Thread{
                run { startlisten() }
            }.start()
        }


        iptv.setOnClickListener {
            Thread{
                run { udp(ipeditText.text.toString()) }
            }.start()

            Log.e("123",""+getIpAddressString())
        }
    }
    private fun startlisten()
    {
        val buf = ByteArray(1024)
        val packet = DatagramPacket(buf, buf.size)
        val socket = DatagramSocket(12306)
        while(listening) {
            Log.e("ww", "start")
            socket.receive(packet)

            runOnUiThread { iptv.text = String(buf, 0, buf.size) }
            val p = DatagramPacket(buf, buf.size, packet.getAddress(), packet.getPort())
            Log.e("ww", "send")
            socket.send(p)
        }
        socket.close()
    }


    private fun udp(ip:String) {
        val bytes: ByteArray = editText.text.toString().toByteArray()
        try {
            /*******************发送数据 */
            val address: InetAddress = InetAddress.getByName(ip)
            //1.构造数据包
            val packet =
                DatagramPacket(bytes, bytes.size, address, 12306)
            //2.创建数据报套接字并将其绑定到本地主机上的指定端口。
            val socket = DatagramSocket()
            //3.从此套接字发送数据报包。
            socket.send(packet)
            /*******************接收数据 */ //1.构造 DatagramPacket，用来接收长度为 length 的数据包。
            val bytes1 = ByteArray(1024)
            val receiverPacket =
                DatagramPacket(bytes1, bytes1.size)
            socket.receive(receiverPacket)
            socket.close()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: SocketException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}