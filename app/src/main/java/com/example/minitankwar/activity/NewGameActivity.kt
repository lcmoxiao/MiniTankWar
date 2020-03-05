package com.example.minitankwar.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS.Loge
import com.example.minitankwar.TOOLS.gameMode
import com.example.minitankwar.TOOLS.getIpAddressString
import com.example.minitankwar.TOOLS.meTankID
import com.example.minitankwar.UDPManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_newgame.*



class NewGameActivity : AppCompatActivity() {

    private var listening:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newgame)
        initInfo()
        initClick()
        UDPManager.init()
    }

    override fun onRestart() {
        super.onRestart()
        initInfo()
    }

    private fun initInfo(){
        createRoom.text = "创建房间"
        joinRoom.text = "加入房间"
        startgame.text="开始游戏"
        readygame.text = "准备"
        meIP.text = ("本地IP:"+getIpAddressString())
        startgame.visibility = INVISIBLE
        readygame.visibility = INVISIBLE
        createRoom.isEnabled = true
        joinRoom.isEnabled = true
        joinIp.isEnabled = true
        listening = false
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
                                    createRoom.text =("已连接:"+UDPManager.getHisIP())
                                    joinRoom.text = "等待对方准备"
                                }
                                meTankID = 0
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
                    meTankID = 1
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
            if(UDPManager.listenPacket.address.toString().compareTo(UDPManager.getHisIP().toString())==0) {
                meTankID = 0
                gameMode = 0
            }
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
                if(UDPManager.listenPacket.address.toString().compareTo(UDPManager.getHisIP().toString())!=0) {
                    val intent = Intent(this, GameActivity::class.java)
                    startActivity(intent)
                }
            } }.start()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        listening = false
        gameMode = 0
        meTankID = 0
    }
}