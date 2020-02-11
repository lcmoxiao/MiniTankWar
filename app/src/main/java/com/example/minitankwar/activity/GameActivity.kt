package com.example.minitankwar.activity


import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.minitankwar.CrashDetector.Companion.bullets
import com.example.minitankwar.CrashDetector.Companion.tanks
import com.example.minitankwar.CrashDetector.Companion.walls
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS
import com.example.minitankwar.TOOLS.dp250
import com.example.minitankwar.TOOLS.dp40
import com.example.minitankwar.TOOLS.gameMode
import com.example.minitankwar.TOOLS.getBulletType
import com.example.minitankwar.TOOLS.getBulletTypeInt
import com.example.minitankwar.TOOLS.getIntByStringFromJson
import com.example.minitankwar.TOOLS.listenPacket
import com.example.minitankwar.TOOLS.listenSocket
import com.example.minitankwar.TOOLS.recv
import com.example.minitankwar.TOOLS.send
import com.example.minitankwar.TOOLS.sendSocket
import com.example.minitankwar.TOOLS.tmpTankID
import com.example.minitankwar.gameInfo.gamerole.Bullet
import com.example.minitankwar.gameInfo.gamerole.Tank
import com.example.minitankwar.gameInfo.gamerole.Wall
import com.example.minitankwar.gameInfo.gamerole.buttle.LaserGun
import com.example.minitankwar.gameInfo.gamerole.buttle.RocketGun
import com.example.minitankwar.gameInfo.gamerole.buttle.ShotGun
import kotlinx.android.synthetic.main.activity_game.*
import org.json.JSONObject


class GameActivity :AppCompatActivity(){

    private val listenBuf = ByteArray(1024)

    private var tankBody  = ArrayList<View>()
    private var tankBarrel   = ArrayList<View>()
    private val myBulletsViews = ArrayList<View>()

    private val scanIng = 999
    //子弹类别
    private var gunType = TOOLS.GunType.Shot
    private var lastGunType = TOOLS.GunType.Shot
    //摇杆初始化
    inner class MoveButton(view:View):HoverButtonHelp(view){
        override fun doInHovering() {
            val tmpPosition = tanks[tankId].clone()
            when (tanks[tankId].updateTankPosition(this.buttonDirection,tankBody[tankId],tankBarrel[tankId])) {
                TOOLS.CrashType.NoCrash -> {

                }
                else -> tanks[tankId].copyPositionData(tmpPosition)
            }
            Thread{
                if(gameMode==1) { sendMyMsg(0) }
            }.start()
        }
    }

    inner class BackButton(view:View):HoverButtonHelp(view){
        override fun doInDown() {
            opbuttonbrand.setImageDrawable(getDrawable(R.drawable.pink_circle))
        }
        override fun doInHovering() {
            val tmpPosition = tanks[tankId].clone()
            when (tanks[tankId].moveBack(tankBody[tankId],tankBarrel[tankId])) {
                TOOLS.CrashType.NoCrash -> {

                }
                else -> tanks[tankId].copyPositionData(tmpPosition)
            }
            Thread{
                if(gameMode==1) { sendMyMsg(0) }
            }.start()
        }
        override fun doInUp() {
            opbuttonbrand.setImageDrawable(getDrawable(R.drawable.opbuttonbrand))
        }
    }
    inner class BarrelButton(view:View):HoverButtonHelp(view){
        override fun doInHovering() {
            if(tanks[tankId].getBarrelDirection() != buttonDirection) {
                tanks[tankId].updateBarrelDiffDirection(buttonDirection)
                tankBarrel[tankId].rotation = -tanks[tankId].getBarrelDirection().toFloat()
            }
            Thread{
                if(gameMode==1) { sendMyMsg(0) }
            }.start()
        }
    }

    //坦克ID
    var tankId: Int = tmpTankID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        initHoverButton()
        initGunButtonView()
        initTankInformation()
        initWallInformation()
        initClickListener()
        //子弹扫描
        bulletScanHandler.sendEmptyMessage(scanIng)
        tankScanHandler.sendEmptyMessage(scanIng)
        //信息接收
        Thread{
            while(true) {
                recvMsg()
            }
        }.start()
    }

    private fun initWallInformation() {
        addWall(dp250,dp250)
        addWall(dp40,dp250)
    }

    override fun onDestroy() {
        super.onDestroy()
        world.removeAllViews()
        tankBody.clear()
        tankBarrel.clear()
        myBulletsViews.clear()
        walls.clear()
        bullets.clear()
        tanks.clear()
    }

    //初始化悬停
    private fun initHoverButton(){
        MoveButton(opbutton).build()
        BackButton(opbuttonbrand).setCanMove(false).build()
        BarrelButton(atbutton).build()
    }

    //初始化选中散弹按钮，刷新View
    private fun initGunButtonView()
    {
        buttonbullet1.background = getDrawable(R.drawable.checkbullet)
    }



    //初始化坦克
    private fun initTankInformation(){
        tankBody.add(getViewById(R.layout.tank))
        tankBarrel.add(getViewById(R.layout.tankbarrel))
        addTank(0,500,500,0)
        if(gameMode==1) {
            tankBody.add(getViewById(R.layout.tank))
            tankBarrel.add(getViewById(R.layout.tankbarrel))
            addTank(1, 800, 1000, 45)
        }
    }

    //更新子弹选择按钮
    private fun unCheckButtonView(gunType: TOOLS.GunType){
        when (gunType) {
            TOOLS.GunType.Shot -> {
                buttonbullet1.background = getDrawable(R.drawable.uncheckbullet)
            }
            TOOLS.GunType.Rocket -> {
                buttonbullet2.background = getDrawable(R.drawable.uncheckbullet)
            }
            TOOLS.GunType.Laser  -> {
                buttonbullet3.background = getDrawable(R.drawable.uncheckbullet)
            }
        }
    }

    private fun initClickListener()
    {
        buttonbullet1.setOnClickListener {
            if(gunType!=TOOLS.GunType.Shot)
            {
                lastGunType = gunType
                gunType = TOOLS.GunType.Shot
                it.background = getDrawable(R.drawable.checkbullet)
                unCheckButtonView(lastGunType)
            }
        }
        buttonbullet2.setOnClickListener {
            if(gunType!=TOOLS.GunType.Rocket)
            {
                lastGunType = gunType
                gunType = TOOLS.GunType.Rocket
                it.background = getDrawable(R.drawable.checkbullet)
                unCheckButtonView(lastGunType)
            }
        }
        buttonbullet3.setOnClickListener {
            if(gunType!=TOOLS.GunType.Laser)
            {
                lastGunType = gunType
                gunType = TOOLS.GunType.Laser
                it.background = getDrawable(R.drawable.checkbullet)
                unCheckButtonView(lastGunType)
            }
        }
        atbuttonbrand.setOnClickListener {
            addBullet(gunType,tankId)
            Thread{
                if(gameMode==1) { sendMyMsg(1) }
            }.start()
        }
    }

    //信息發送前處理
    private fun infoToByteArrayByJson(tankId: Int,msgType:Int):ByteArray{
        val js:JSONObject = if(msgType==0) {
            tanks[tankId].toJson().put("msgType", msgType)
        }else{
            tanks[tankId].toJson().put("msgType", msgType).put("gunType",getBulletTypeInt(gunType))
        }
        return js.toString().toByteArray()
}

    //信息接收后
    private fun infoGetJsonByByteArray(tankId: Int){
        val js = JSONObject(String(listenBuf,0,listenBuf.size))
        tanks[tankId].copyByJsonFromByteArray(listenBuf)
        runOnUiThread {   tanks[tankId].setView(tankBody[tankId],tankBarrel[tankId])}
        if(getIntByStringFromJson(js,"msgType")==1) {
            runOnUiThread {
                addBullet(getBulletType(getIntByStringFromJson(js, "gunType")),tankId)
            }
        }
    }

    private fun sendMyMsg(msgType:Int){
        if(tankId==0)send(infoToByteArrayByJson(0,msgType), listenSocket, listenPacket.address, listenPacket.port)
        else send(infoToByteArrayByJson(1,msgType),listenSocket, TOOLS.sendPacket.address, TOOLS.sendPacket.port)
    }

    private fun recvMsg(){
        if(tankId==0) {
            recv(listenSocket, listenBuf)
            infoGetJsonByByteArray(1)
        }else{
            recv(sendSocket,listenBuf)
            infoGetJsonByByteArray(0)
        }
    }

    //坦克生死扫描器
    private var tankScanHandler = Handler {
        when (it.what) {
            scanIng->
            {
                tankScan()
                true
            }
            else -> true
        }
    }

    //扫描坦克生死,并刷新UI
    private fun tankScan(){
        if(tanks.size<=1)return
        when {
            tanks[1].isDead() -> {
                tanks[1].removeViewFrom(world,tankBody[1],tankBarrel[1])
                if(tankId==0) Toast.makeText(baseContext, "you win", Toast.LENGTH_SHORT).show()
                else Toast.makeText(baseContext, "you dead", Toast.LENGTH_SHORT).show()
            }
            tanks[0].isDead() -> {
                tanks[0].removeViewFrom(world,tankBody[0],tankBarrel[0])
                if(tankId==0) Toast.makeText(baseContext, "you dead", Toast.LENGTH_SHORT).show()
                else Toast.makeText(baseContext, "you win", Toast.LENGTH_SHORT).show()
            }
            else -> tankScanHandler.sendEmptyMessageDelayed(scanIng,10)
        }
    }

    //子弹扫描器
    private var bulletScanHandler = Handler {
        when (it.what) {
            scanIng->
            {
                bulletScan()
                true
            }
            else -> true
        }
    }

    //扫描死亡的子弹并清除，并检测是否没有了子弹而停止扫描
    private fun bulletScan(){
        var i = 0
        var size = bullets.size
        while(i< size)
        {
            val it = bullets[i]
            if (!it.bulletLiving) {
                world.removeView(myBulletsViews[i])
                bullets.remove(it)
                myBulletsViews.removeAt(i)
                size--
                continue
            }
            i++
        }
        i = 0
        size = bullets.size
        while(i< size)
        {
            val it = bullets[i]
            when (it.updateBulletPosition(myBulletsViews[i])) {
                TOOLS.CrashType.NoCrash -> {

                }
                else -> {
                    it.bulletLiving = false
                }
            }
            i++
        }
        bulletScanHandler.sendEmptyMessageDelayed(scanIng,5)
    }

    //添加坦克信息，并初始化View
    private fun addTank(tankId: Int,x:Int,y:Int,direction:Int)
    {
        tanks.add(Tank(tankId,x,y,direction))//Tank创建
        tanks[tankId].initInfoAndView(world,tankBody[tankId],tankBarrel[tankId])
    }

    //添加子彈
    private fun addBullet(gunType: TOOLS.GunType, tankId: Int){
        val bulletId = bullets.size
        when (gunType) {
            TOOLS.GunType.Laser ->
            {
                myBulletsViews.add(getViewById(R.layout.laserbullet))
                bullets.add(LaserGun(tankId))
                bullets[bulletId].initInfoAndView(tanks[tankId],0,world,myBulletsViews[bulletId])
            }
            TOOLS.GunType.Shot ->{
                myBulletsViews.add(getViewById(R.layout.shotbullet))
                myBulletsViews.add(getViewById(R.layout.shotbullet))
                myBulletsViews.add(getViewById(R.layout.shotbullet))
                bullets.add(ShotGun(tankId))
                bullets[bulletId].initInfoAndView(tanks[tankId],0,world,myBulletsViews[bulletId])
                bullets.add(ShotGun(tankId))
                bullets[bulletId+1].initInfoAndView(tanks[tankId],30,world,myBulletsViews[bulletId+1])
                bullets.add(ShotGun(tankId))
                bullets[bulletId+2].initInfoAndView(tanks[tankId],-30,world,myBulletsViews[bulletId+2])
            }
            TOOLS.GunType.Rocket -> {
                myBulletsViews.add(getViewById(R.layout.rocketbullet))
                bullets.add(RocketGun(tankId))
                bullets[bulletId].initInfoAndView(tanks[tankId],0,world,myBulletsViews[bulletId])
            }
        }
    }

    //添加墙，并初始化View
    private fun addWall(x:Int,y:Int)
    {
        walls.add(Wall(x,y,getViewById(R.layout.wall)))
        walls[walls.size-1].initInfoAndView(world)
    }

    private fun getViewById(ViewId:Int): View {
        return layoutInflater.inflate(ViewId,world,false)
    }



}