package com.example.minitankwar

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object UDPManager{
    private lateinit var listenSocket:DatagramSocket

    lateinit var listenPacket : DatagramPacket  //等待监听的包
    private lateinit var sendPacket : DatagramPacket    //发往目的地的包
    val RecvBuf = ByteArray(1024)
    val ReplyBuf = ByteArray(1024)
    private var dstIP = "127.0.0.1" //默认目的IP
    private var dstPort = 12306     //默认目的端口

    fun init(){
        listenSocket  = DatagramSocket(12306)//监听用的socket
    }

    fun init(port:Int)
    {
        listenSocket  = DatagramSocket(port)//监听用的socket
    }

    fun setIPandPort(dstIP:String,dstPort:Int)
    {
        this.dstIP =dstIP
        this.dstPort = dstPort
    }

    fun close(){
        listenSocket.close()
        listenSocket.close()
    }

    //使用12307端口向目标设置并发送信息
    fun sendMsg(str:String){
        val bytes = str.toByteArray()
        sendPacket = DatagramPacket(bytes,bytes.size, InetAddress.getByName(dstIP), dstPort)
        listenSocket.send(sendPacket)
    }
    fun sendMsg(bytes: ByteArray){
        sendPacket = DatagramPacket(bytes,bytes.size, InetAddress.getByName(dstIP), dstPort)
        listenSocket.send(sendPacket)
    }

    //使用12306端口回复目标信息(前提是接收过信息)
    fun sendReplyMsg(str:String){
        val bytes = str.toByteArray()
        listenPacket = DatagramPacket(bytes,bytes.size, listenPacket.address, listenPacket.port)
        listenSocket.send(listenPacket)
    }
    fun sendReplyMsg(bytes: ByteArray){
        listenPacket = DatagramPacket(bytes,bytes.size, listenPacket.address, listenPacket.port)
        listenSocket.send(listenPacket)
    }

    //来自服务器回复的信息接收
    fun recvReplyMsg():ByteArray
    {
        recv(listenSocket,ReplyBuf)
        return ReplyBuf
    }

    //来自客户端的信息
    fun recvMsg():ByteArray
    {
        listenPacket=recv(listenSocket,RecvBuf)
        return RecvBuf
    }

    //获得对方的IP
    fun getHisIP():InetAddress{
        return listenPacket.address
    }

    private fun recv(socket: DatagramSocket, receiveBuf: ByteArray): DatagramPacket {
        val receiverPacket = DatagramPacket(receiveBuf, receiveBuf.size)
        socket.receive(receiverPacket)
        return receiverPacket
    }
}