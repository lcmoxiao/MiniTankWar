package com.example.minitankwar.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS.gameMode
import com.example.minitankwar.TOOLS.getIpAddressString
import com.example.minitankwar.TOOLS.listenPacket
import com.example.minitankwar.TOOLS.listenSocket
import com.example.minitankwar.TOOLS.recv
import com.example.minitankwar.TOOLS.send
import com.example.minitankwar.TOOLS.sendPacket
import com.example.minitankwar.TOOLS.sendSocket
import com.example.minitankwar.TOOLS.tmpTankID
import kotlinx.android.synthetic.main.activity_newgame.*
import java.io.IOException
import java.net.*


class NewGameActivity : AppCompatActivity() {


    private var listening = false
    private val listenBuf = ByteArray(1024)
    private val sendRecBuf = ByteArray(1024)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)
        meIP.text = ("本地IP:"+getIpAddressString())
        initClick()
    }

    private fun initClick()
    {
        createRoom.setOnClickListener {
            if(!listening)
            {
                createRoom.text = "正在监听"
                createRoom.isEnabled = false
                listening=true
                Thread{
                    run { startListen() }
                }.start()
            }
        }

        joinRoom.setOnClickListener {

            Thread{ run { udp("join",joinIp.text.toString()) } }.start()
        }

        startgame.setOnClickListener {
            Thread{ run { send("start",listenSocket,listenPacket.address,listenPacket.port) } }.start()
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        readygame.setOnClickListener {
            Thread{ run { udp("ready",joinIp.text.toString()) } }.start()
        }

    }





    private fun startListen()
    {
        while(listening) {
            listenPacket = recv(listenSocket,listenBuf)
            if("join".compareTo(String(listenBuf, 0, 4))==0) {
                runOnUiThread {
                    tmpTankID = 0
                    gameMode = 1
                    createRoom.text =("已连接:"+listenPacket.address)
                    joinRoom.text = "等待对方准备"
                }
                send("joinover",listenSocket,listenPacket.address,listenPacket.port)
            }else  if("ready".compareTo(String(listenBuf, 0, 5))==0) {
                runOnUiThread {
                    joinRoom.text = "对方已准备,可以开始游戏"
                    joinRoom.isEnabled = false
                    startgame.visibility = VISIBLE
                }
                send("readyover",listenSocket,listenPacket.address,listenPacket.port)
                break
            }
        }
    }

    private fun udp(str:String,ip: String) {
        try {
            //发送信息
            val bytes: ByteArray = str.toByteArray()
            sendPacket = DatagramPacket(bytes,bytes.size, InetAddress.getByName(ip), 12306)
            send(str,sendSocket,sendPacket.address,sendPacket.port)
            //接受信息
            sendPacket = recv(sendSocket,sendRecBuf)
            if("joinover".compareTo(String(sendRecBuf, 0, 8))==0)
            {
                runOnUiThread {
                    tmpTankID = 1
                    gameMode = 1
                    readygame.visibility = VISIBLE
                    joinIp.isEnabled = false
                    joinRoom.text = "请点击准备"
                    joinRoom.isEnabled = false
                }
            }else if("readyover".compareTo(String(sendRecBuf, 0, 9))==0)
            {
                runOnUiThread {
                    readygame.text = "已准备，等待房主开始"
                }
                recv(sendSocket,sendRecBuf)
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: SocketException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listening = false
    }
}