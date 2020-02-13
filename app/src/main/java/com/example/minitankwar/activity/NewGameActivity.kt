package com.example.minitankwar.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS.gameMode
import com.example.minitankwar.TOOLS.getIpAddressString
import com.example.minitankwar.TOOLS.tmpTankID
import com.example.minitankwar.UDPManager
import kotlinx.android.synthetic.main.activity_newgame.*



class NewGameActivity : AppCompatActivity() {

    private var listening = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)
        meIP.text = ("本地IP:"+getIpAddressString())
        initClick()
        UDPManager.init()
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
                    run {
                        while(listening) {
                            UDPManager.recvMsg()
                            if("join".compareTo(String(UDPManager.RecvBuf, 0, 4))==0) {
                                runOnUiThread {
                                    createRoom.text =("已连接:"+UDPManager.getHisIP().address)
                                    joinRoom.text = "等待对方准备"
                                }
                                tmpTankID = 0
                                gameMode = 1
                                UDPManager.sendReplyMsg("joinover")
                            }else  if("ready".compareTo(String(UDPManager.RecvBuf, 0, 5))==0) {
                                runOnUiThread {
                                    joinRoom.text = "对方已准备,可以开始游戏"
                                    joinRoom.isEnabled = false
                                    startgame.visibility = VISIBLE
                                }
                                UDPManager.sendReplyMsg("readyover")
                                break
                            }
                        }
                    }
                }.start()
            }
        }

        joinRoom.setOnClickListener {
            Thread{
                UDPManager.setIPandPort(joinIp.text.toString(),12306)
                UDPManager.sendMsg("join")
                UDPManager.recvReplyMsg()
                if("joinover".compareTo(String(UDPManager.ReplyBuf, 0, 8))==0)
                {
                    tmpTankID = 1
                    gameMode = 1
                    runOnUiThread {
                        readygame.visibility = VISIBLE
                        joinIp.isEnabled = false
                        joinRoom.text = "请点击准备"
                        joinRoom.isEnabled = false
                    }
                }
             }.start()
        }

        startgame.setOnClickListener {
            Thread{ run { UDPManager.sendReplyMsg("start") } }.start()
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        readygame.setOnClickListener {
            Thread{
            UDPManager.sendMsg("ready")
            UDPManager.recvReplyMsg()
            if("readyover".compareTo(String(UDPManager.ReplyBuf, 0, 9))==0)
            {
                runOnUiThread {
                    readygame.text = "已准备，等待房主开始"
                }
                UDPManager.recvReplyMsg()
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            } }.start()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        listening = false
    }
}