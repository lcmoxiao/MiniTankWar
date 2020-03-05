package com.example.minitankwar.activity

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.minitankwar.CrashDetector.Companion.bullets
import com.example.minitankwar.CrashDetector.Companion.tanks
import com.example.minitankwar.CrashDetector.Companion.walls
import com.example.minitankwar.HoverButtonHelp
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS
import com.example.minitankwar.TOOLS.dp250
import com.example.minitankwar.TOOLS.dp40
import com.example.minitankwar.TOOLS.gameMode
import com.example.minitankwar.TOOLS.getBulletType
import com.example.minitankwar.TOOLS.getBulletTypeInt
import com.example.minitankwar.TOOLS.getIntByStringFromJson
import com.example.minitankwar.TOOLS.setViewPosition
import com.example.minitankwar.TOOLS.meTankID
import com.example.minitankwar.UDPManager
import com.example.minitankwar.gameInfo.gamerole.Tank
import com.example.minitankwar.gameInfo.gamerole.Wall
import com.example.minitankwar.gameInfo.gamerole.buttle.LaserGun
import com.example.minitankwar.gameInfo.gamerole.buttle.RocketGun
import com.example.minitankwar.gameInfo.gamerole.buttle.ShotGun
import kotlinx.android.synthetic.main.activity_game.*
import org.json.JSONObject


class GameActivity :AppCompatActivity()
{
    private var tankBody  = ArrayList<View>()
    private var tankBarrel   = ArrayList<View>()
    private val myBulletsViews = ArrayList<View>()
    private var lastShotTime:Long = 0    //上一次射击的时间
    private var lastShotInterval:Long = 0    //射击冷却时间
    private val scanIng = 999   //handler循环命令
    //子弹类别
    private var gunType = TOOLS.GunType.Shot
    private var lastGunType = TOOLS.GunType.Shot
    //这局的坦克ID
    private var tankId: Int = meTankID

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
            while(gameMode==1) {
                recvMsg()
            }
        }.start()
    }

    //摇杆初始化
    inner class MoveButton(view:View): HoverButtonHelp(view){
        override fun doInHovering() {
            val tmpS = tanks[tankId].shape.clone()

            if (tanks[tankId].updateTankPosition(buttonDirection,tankBody[tankId],tankBarrel[tankId]) !=TOOLS.CrashType.NoCrash) {
                tanks[tankId].shape.copyPosAndDir(tmpS)
            }
            Thread{ if(gameMode==1) { sendMyMsg(0) } }.start()
        }
    }
    inner class BackButton(view:View): HoverButtonHelp(view){
        override fun doInDown() {
            opbuttonbrand.setImageDrawable(getDrawable(R.drawable.pink_circle))
        }
        override fun doInHovering() {
            val tmpS = tanks[tankId].shape.clone()
            if (tanks[tankId].moveBack(tankBody[tankId],tankBarrel[tankId]) !=TOOLS.CrashType.NoCrash)
                tanks[tankId].shape.copyPosAndDir(tmpS)
            Thread{ if(gameMode==1) { sendMyMsg(0) } }.start()
        }
        override fun doInUp() {
            opbuttonbrand.setImageDrawable(getDrawable(R.drawable.opbuttonbrand))
        }
    }
    inner class BarrelButton(view:View): HoverButtonHelp(view){
        override fun doInHovering() {
            if(tanks[tankId].getBarrelDirection() != buttonDirection) {
                tanks[tankId].updateBarrelDiffDirection(buttonDirection)
                tankBarrel[tankId].rotation = -tanks[tankId].getBarrelDirection().toFloat()
            }
            if(gameMode==1) Thread{ sendMyMsg(0) }.start()
        }
    }
    //按钮初始化
    private fun initClickListener(){
        atbuttonbrand.setOnClickListener {
            if( System.currentTimeMillis()-lastShotTime >lastShotInterval) {
                lastShotTime = System.currentTimeMillis()
                addBullet(gunType, tankId)
                if (gameMode == 1) Thread { sendMyMsg(1) }.start()  //多人游戏则发出射击信息
            }
        }
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
    }


    private fun initWallInformation() {
        addWall(dp250,dp250)
        addWall(dp40,dp250)
    }


    //初始化悬停
    private fun initHoverButton(){
        MoveButton(opbutton).build()
        BackButton(opbuttonbrand).setCanMove(false).build()
        BarrelButton(atbutton).build()
    }

    //初始化选中散弹按钮，刷新View
    private fun initGunButtonView() {
        buttonbullet1.background = getDrawable(R.drawable.checkbullet)
    }

    //初始化坦克
    private fun initTankInformation(){
        tankBody.add(getViewById(R.layout.tank))
        tankBarrel.add(getViewById(R.layout.tankbarrel))
        addTank(0,500.0,500.0,0.0)
        if(gameMode==1) {
            tankBody.add(getViewById(R.layout.tank))
            tankBarrel.add(getViewById(R.layout.tankbarrel))
            addTank(1, 800.0, 1000.0, 45.0)
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



    //信息發送前處理
    private fun infoToByteArrayByJson(tankId: Int,msgType:Int):ByteArray{
        val js:JSONObject = if(msgType==0) {
            tanks[tankId].shape.toJson().put("msgType", msgType)
        }else{
            tanks[tankId].shape.toJson().put("msgType", msgType).put("gunType",getBulletTypeInt(gunType))
        }
        return js.toString().toByteArray()
    }

    //信息接收后
    private fun infoGetJsonByByteArray(tankId: Int){
        val js:JSONObject
        if(tankId==1) {
            js = JSONObject(String(UDPManager.RecvBuf, 0, UDPManager.RecvBuf.size))
            tanks[tankId].shape.copyByJson(js)
        }else
        {
            js = JSONObject(String(UDPManager.ReplyBuf, 0, UDPManager.ReplyBuf.size))
            tanks[tankId].shape.copyByJson(js)
        }
        runOnUiThread {   tanks[tankId].setView(tankBody[tankId],tankBarrel[tankId])}
        if(getIntByStringFromJson(js,"msgType")==1) {
            runOnUiThread {
                addBullet(getBulletType(getIntByStringFromJson(js, "gunType")),tankId)
            }
        }
    }

    //发送信息，包括类型
    private fun sendMyMsg(msgType:Int){
        if(tankId==0)UDPManager.sendReplyMsg(infoToByteArrayByJson(0,msgType))
        else UDPManager.sendMsg(infoToByteArrayByJson(1,msgType))
    }

    //收到信息处理
    private fun recvMsg(){
        if(tankId==0) {
            UDPManager.recvMsg()
            infoGetJsonByByteArray(1)
        }else{
            UDPManager.recvReplyMsg()
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
        if(gameMode==0)return
        when {
            tanks[1].isDead() -> {
                gameMode=0
                tanks[1].removeViewFrom(world,tankBody[1],tankBarrel[1])
                if(tankId==0) Toast.makeText(baseContext, "you win", Toast.LENGTH_SHORT).show()
                else Toast.makeText(baseContext, "you dead", Toast.LENGTH_SHORT).show()
            }
            tanks[0].isDead() -> {
                gameMode=0
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

    //子弹爆炸动画
    private fun boom(x:Double,y:Double,size:Double){
        val a = layoutInflater.inflate(R.layout.boom,world,false)
        runOnUiThread {
            setViewPosition(a,x,y)
            world.addView(a)
        }
        val valueAnimator= ValueAnimator.ofFloat(5.0.toFloat(),10.0.toFloat(),2*size.toFloat())
        valueAnimator.duration = 100
        valueAnimator.addUpdateListener {
            a.scaleX = it .animatedValue as Float
            a.scaleY = it .animatedValue as Float
            a.requestLayout()
            if(it .animatedValue as Float==2*size.toFloat()) runOnUiThread { world.removeView(a) }
        }
        valueAnimator.start()
    }

    //扫描死亡的子弹并清除，并检测是否没有了子弹而停止扫描
    private fun bulletScan(){
        var i = 0
        var size = bullets.size
        while(i< size)
        {
            val it = bullets[i]
            if (!it.bulletLiving) {
                boom(bullets[i].shape.getX(),bullets[i].shape.getY(), bullets[i].shape.width)
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
    private fun addTank(tankId: Int,x:Double,y:Double,direction:Double)
    {
        tanks.add(Tank(tankId,x,y,direction))//Tank创建
        tanks[tankId].initShapeAndViewParam(world,tankBody[tankId],tankBarrel[tankId])
    }

    //添加子彈
    private fun addBullet(gunType: TOOLS.GunType, tankId: Int){
        val bulletId = bullets.size
        when (gunType) {
            TOOLS.GunType.Shot ->{
                myBulletsViews.add(getViewById(R.layout.shotbullet))
                bullets.add(ShotGun(tankId))
                bullets[bulletId].initInfoAndView(tanks[tankId],0,world,myBulletsViews[bulletId])
                myBulletsViews.add(getViewById(R.layout.shotbullet))
                bullets.add(ShotGun(tankId))
                bullets[bulletId+1].initInfoAndView(tanks[tankId],30,world,myBulletsViews[bulletId+1])
                myBulletsViews.add(getViewById(R.layout.shotbullet))
                bullets.add(ShotGun(tankId))
                bullets[bulletId+2].initInfoAndView(tanks[tankId],-30,world,myBulletsViews[bulletId+2])
                lastShotInterval = bullets[bulletId].loadInterval
            }
            TOOLS.GunType.Rocket -> {
                myBulletsViews.add(getViewById(R.layout.rocketbullet))
                bullets.add(RocketGun(tankId))
                bullets[bulletId].initInfoAndView(tanks[tankId],0,world,myBulletsViews[bulletId])
                lastShotInterval = bullets[bulletId].loadInterval
            }
            TOOLS.GunType.Laser ->
            {
                myBulletsViews.add(getViewById(R.layout.laserbullet))
                bullets.add(LaserGun(tankId))
                bullets[bulletId].initInfoAndView(tanks[tankId],0,world,myBulletsViews[bulletId])
                lastShotInterval = bullets[bulletId].loadInterval
            }
        }
    }

    //添加墙，并初始化View
    private fun addWall(x:Double,y:Double)
    {
        walls.add(Wall(x,y,getViewById(R.layout.wall)))
        walls[walls.size-1].initInfoAndView(world)
    }

    private fun getViewById(ViewId:Int): View {
        return layoutInflater.inflate(ViewId,world,false)
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
        gameMode=0
    }
}