package com.example.minitankwar.activity


import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.minitankwar.CrashDetector.Companion.bullets
import com.example.minitankwar.CrashDetector.Companion.tanks
import com.example.minitankwar.CrashDetector.Companion.walls
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS
import com.example.minitankwar.TOOLS.BARREL_DETECT
import com.example.minitankwar.TOOLS.BULLET_SCAN
import com.example.minitankwar.TOOLS.MOVE_BACK
import com.example.minitankwar.TOOLS.MOVE_DETECT
import com.example.minitankwar.gameInfo.gamerole.Tank
import com.example.minitankwar.TOOLS.getDirectionByTan
import com.example.minitankwar.TOOLS.setViewPosition
import com.example.minitankwar.gameInfo.gamerole.Bullet
import com.example.minitankwar.gameInfo.gamerole.buttle.LaserGun
import com.example.minitankwar.gameInfo.gamerole.buttle.RocketGun
import com.example.minitankwar.gameInfo.gamerole.buttle.ShotGun
import kotlinx.android.synthetic.main.activity_joingame.*


class JoinGameActivity :AppCompatActivity(){

    //子弹类别
    private var gunType = TOOLS.GunType.Shot
    //是否保持探测，用于悬浮按钮的探测(因为touchListener不支持悬停，便使用handler)
    private var isMoveDetecting = true
    private var isBarrelDetecting = true
    //摇杆信息
    private var opButtonX = 0
    private var opButtonY = 0
    private var opButtonDirection  = -1
    private var atButtonX = 0
    private var atButtonY = 0
    private var atButtonDirection  = -1
    //事件原始位置
    private var opTouchX = 0
    private var opTouchY = 0
    private var atTouchX = 0
    private var atTouchY = 0
    //坦克ID
    private var tankId:Int = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joingame)
        initButtonXY()
        initGunButtonView()
        initTankInformation()
        initClickListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        walls.clear()
        bullets.clear()
        tanks.clear()
    }

    //记录摇杆原始位置
    private fun initButtonXY(){
        var params = opbutton.layoutParams as FrameLayout.LayoutParams
        opButtonX = params.leftMargin
        opButtonY = params.topMargin
        params = atbutton.layoutParams as FrameLayout.LayoutParams
        atButtonX = params.leftMargin
        atButtonY = params.topMargin
    }

    //初始化选中散弹按钮，刷新View
    private fun initGunButtonView()
    {
        gunType = TOOLS.GunType.Shot
        updateBulletButtonView(gunType)
    }

    //初始化坦克
    private fun initTankInformation(){
        addTank(tankId,500,1000,0)
        addTank(1,800,1000,45)
    }

    //添加坦克信息，并初始化View
    private fun addTank(tankId: Int,x:Int,y:Int,direction:Int)
    {
        tanks.add(Tank(tankId,x,y,direction,getViewById(R.layout.tank),getViewById(R.layout.tankbarrel)))//Tank创建
        tanks[tankId].initInfoAndView(world)
    }

    //更新子弹选择按钮
    private fun updateBulletButtonView(gunType: TOOLS.GunType){
        when (gunType) {
            TOOLS.GunType.Shot -> {
                buttonbullet1.background = getDrawable(R.drawable.checkbullet)
                buttonbullet2.background = getDrawable(R.drawable.uncheckbullet)
                buttonbullet3.background = getDrawable(R.drawable.uncheckbullet)
            }
            TOOLS.GunType.Rocket -> {
                buttonbullet1.background = getDrawable(R.drawable.uncheckbullet)
                buttonbullet2.background = getDrawable(R.drawable.checkbullet)
                buttonbullet3.background = getDrawable(R.drawable.uncheckbullet)
            }
            else -> {
                buttonbullet1.background = getDrawable(R.drawable.uncheckbullet)
                buttonbullet2.background = getDrawable(R.drawable.uncheckbullet)
                buttonbullet3.background = getDrawable(R.drawable.checkbullet)
            }
        }
    }

    //添加子弹信息，初始化bulletView,并激活handler
    private fun shot(gunType: TOOLS.GunType,tank: Tank){
        val bulletId = bullets.size
        when (gunType) {
            TOOLS.GunType.Laser ->
            {
                bullets.add(LaserGun(getViewById(R.layout.laserbullet),tank.tankId))
                bullets[bulletId].initBulletInfoAndView(tank,0,world)
                if(bullets.size==1)handler.sendEmptyMessage(BULLET_SCAN)
            }
            TOOLS.GunType.Shot ->{
                bullets.add(ShotGun(getViewById(R.layout.shotbullet),tank.tankId))
                bullets[bulletId].initBulletInfoAndView(tank,0,world)
                bullets.add(ShotGun(getViewById(R.layout.shotbullet),tank.tankId))
                bullets[bulletId+1].initBulletInfoAndView(tank,30,world)
                bullets.add(ShotGun(getViewById(R.layout.shotbullet),tank.tankId))
                bullets[bulletId+2].initBulletInfoAndView(tank,-30,world)
                if(bullets.size==3)handler.sendEmptyMessage(BULLET_SCAN)
            }
            TOOLS.GunType.Rocket -> {
                bullets.add(RocketGun(getViewById(R.layout.rocketbullet),tank.tankId))
                bullets[bulletId].initBulletInfoAndView(tank,0,world)
                if(bullets.size==1)handler.sendEmptyMessage(BULLET_SCAN)
            }
        }

    }

    private fun initClickListener()
    {
        buttonbullet1.setOnClickListener {
            gunType = TOOLS.GunType.Shot
            updateBulletButtonView(gunType)
        }
        buttonbullet2.setOnClickListener {
            gunType = TOOLS.GunType.Rocket
            updateBulletButtonView(gunType)
        }
        buttonbullet3.setOnClickListener {
            gunType = TOOLS.GunType.Laser
            updateBulletButtonView(gunType)
        }

        opbutton.setOnTouchListener { v , event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //开始探测，设置探测标志位为true
                    isMoveDetecting = true
                    handler.sendEmptyMessage(MOVE_DETECT)
                    opTouchX = event.rawX.toInt()
                    opTouchY = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    setViewPosition(v,opButtonX,opButtonY,event.rawX.toInt() - opTouchX,event.rawY.toInt() - opTouchY)
                    opButtonDirection = getDirectionByTan(v.x.toInt(),v.y.toInt(),opButtonX,opButtonY)
                }
                MotionEvent.ACTION_UP -> {
                    isMoveDetecting = false                 //设置探测标志位为false，结束探测
                    setViewPosition(v,opButtonX,opButtonY)  //摇杆归位
                }
            }
            true
        }

        atbutton.setOnTouchListener { v , event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //开始探测，设置探测标志位为true
                    isBarrelDetecting = true
                    handler.sendEmptyMessage(BARREL_DETECT)
                    atTouchX = event.rawX.toInt()
                    atTouchY = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    setViewPosition(v,atButtonX,atButtonY,event.rawX.toInt() - atTouchX,event.rawY.toInt() - atTouchY)
                    atButtonDirection = getDirectionByTan(v.x.toInt(),v.y.toInt(),atButtonX,atButtonY)
                }
                MotionEvent.ACTION_UP -> {
                    //设置探测标志位为false，结束探测
                    isBarrelDetecting = false
                    setViewPosition(v,atButtonX,atButtonY)  //摇杆归位
                }
            }
            true
        }
        opbuttonbrand.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //开始探测，设置探测标志位为true
                    isMoveDetecting = true
                    handler.sendEmptyMessage(MOVE_BACK)
                    opbuttonbrand.setImageDrawable(getDrawable(R.drawable.pink_circle))
                }
                MotionEvent.ACTION_UP -> {
                    opbuttonbrand.setImageDrawable(getDrawable(R.drawable.opbuttonbrand))
                    isMoveDetecting = false                 //设置探测标志位为false，结束探测
                }
            }
            true
        }

        atbuttonbrand.setOnClickListener {
            shot(gunType,tanks[tankId])
        }
    }

    //这是一个会自己给自己发信息的无限循环handler
    private var handler = Handler {
        when (it.what) {
            MOVE_DETECT ->
            {
                tankMove(opButtonDirection,tankId)
                true
            }
            BARREL_DETECT ->
            {
                barrelMove(atButtonDirection,tankId)
                true
            }
            BULLET_SCAN->
            {
                bulletScan()
                true
            }
            MOVE_BACK->
            {
                tankMoveBack(tankId)
                true
            }
            else -> true
        }
    }

    //给出方向角度之后，会通过内置函数，来改变tank的位置
    //在移动的时候，将坦克的x，y坐标与marginLeft和marginTop同步。
    private fun tankMove(newDirection:Int,tankId: Int){
        if(isMoveDetecting){
            val tmpPosition = tanks[tankId].clone()
            when (tanks[tankId].updateTankPosition(newDirection)) {
                TOOLS.CrashType.NoCrash -> {
                    tanks[tankId].setView()
                    tanks[tankId].tankBody.rotation = -tanks[tankId].direction.toFloat()
                    tanks[tankId].tankBarrel.rotation = -tanks[tankId].direction.toFloat() - tanks[tankId].barrelDiffDirection
                }
                TOOLS.CrashType.Border -> {
                    tanks[tankId].copyPositionData(tmpPosition)
                }
                TOOLS.CrashType.Tank -> {
                    tanks[tankId].copyPositionData(tmpPosition)
                }
                else -> {}
            }
            handler.sendEmptyMessageDelayed(MOVE_DETECT, 10)
        }
    }

    private fun tankMoveBack(tankId: Int){
        if(isMoveDetecting){
            val tmpPosition = tanks[tankId].clone()
            when (tanks[tankId].moveBack()) {
                TOOLS.CrashType.NoCrash -> {
                    tanks[tankId].setView()
                }
                TOOLS.CrashType.Border -> {
                    tanks[tankId].copyPositionData(tmpPosition)
                }
                TOOLS.CrashType.Tank -> {
                    tanks[tankId].copyPositionData(tmpPosition)
                }
                else -> {}
            }
            handler.sendEmptyMessageDelayed(MOVE_BACK, 10)
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
                val view = it.bulletView
                world.removeView(view)
                bullets.remove(it)
                size--
                continue
            }
            i++
        }
        if(bullets.size!=0){
            bullets.forEach {
                bulletMove(it)
            }
            handler.sendEmptyMessageDelayed(BULLET_SCAN,5)
        }
    }

    //移动子弹，并标记死亡的子弹
    private fun bulletMove(bullet: Bullet){
        val bulletId = bullets.indexOf(bullet)
        bullets[bulletId].setView()
        when (bullets[bulletId].updateBulletPosition()) {
            TOOLS.CrashType.NoCrash -> {

            }
            TOOLS.CrashType.Border -> {
                bullets[bulletId].bulletLiving = false
            }
            TOOLS.CrashType.Tank -> {
                bullets[bulletId].bulletLiving = false
            }
            else -> {}
        }
    }

    //移动炮筒
    private fun barrelMove(newDirection:Int,tankId: Int){
        if(isBarrelDetecting){
            if(tanks[tankId].getBarrelDirection() != newDirection) {
                tanks[tankId].updateBarrelDiffDirection(newDirection)
                tanks[tankId].tankBarrel.rotation = -tanks[tankId].getBarrelDirection().toFloat()
            }
            handler.sendEmptyMessageDelayed(BARREL_DETECT, 10)
        }else{
            isBarrelDetecting = true
        }
    }

    private fun getViewById(ViewId:Int): View {
        return layoutInflater.inflate(ViewId,world,false)
    }


}