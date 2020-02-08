package com.example.minitankwar.activity


import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.minitankwar.CrashDetector.Companion.bullets
import com.example.minitankwar.CrashDetector.Companion.tanks
import com.example.minitankwar.CrashDetector.Companion.walls
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS
import com.example.minitankwar.TOOLS.BULLET_SCAN
import com.example.minitankwar.gameInfo.gamerole.Tank
import com.example.minitankwar.gameInfo.gamerole.buttle.LaserGun
import com.example.minitankwar.gameInfo.gamerole.buttle.RocketGun
import com.example.minitankwar.gameInfo.gamerole.buttle.ShotGun
import kotlinx.android.synthetic.main.activity_joingame.*


class JoinGameActivity :AppCompatActivity(){

    //子弹类别
    private var gunType = TOOLS.GunType.Shot
    //摇杆初始化
    inner class MoveButton(view:View):HoverButtonHelp(view){
        override fun doInHovering() {
            val tmpPosition = tanks[tankId].clone()
            when (tanks[tankId].updateTankPosition(this.buttonDirection)) {
                TOOLS.CrashType.NoCrash -> {
                    tanks[tankId].setView()
                    tanks[tankId].tankBody.rotation = -tanks[tankId].direction.toFloat()
                    tanks[tankId].tankBarrel.rotation = -tanks[tankId].getBarrelDirection().toFloat()
                }
                TOOLS.CrashType.Border -> {
                    tanks[tankId].copyPositionData(tmpPosition)
                }
                TOOLS.CrashType.Tank -> {
                    tanks[tankId].copyPositionData(tmpPosition)
                }
                else -> {}
            }
        }
    }

    inner class BackButton(view:View):HoverButtonHelp(view){
        override fun doInDown() {
            opbuttonbrand.setImageDrawable(getDrawable(R.drawable.pink_circle))
        }
        override fun doInHovering() {
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
        }
        override fun doInUp() {
            opbuttonbrand.setImageDrawable(getDrawable(R.drawable.opbuttonbrand))
        }
    }

    inner class BarrelButton(view:View):HoverButtonHelp(view){
        override fun doInHovering() {
            if(tanks[tankId].getBarrelDirection() != buttonDirection) {
                tanks[tankId].updateBarrelDiffDirection(buttonDirection)
                tanks[tankId].tankBarrel.rotation = -tanks[tankId].getBarrelDirection().toFloat()
            }
        }
    }


    //坦克ID
    var tankId: Int = 0

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
        MoveButton(opbutton).build()
        BackButton(opbuttonbrand).setCanMove(false).build()
        BarrelButton(atbutton).build()
    }

    //初始化选中散弹按钮，刷新View
    private fun initGunButtonView()
    {
        gunType = TOOLS.GunType.Shot
        updateBulletButtonView(gunType)
    }

    //初始化坦克
    private fun initTankInformation(){
        addTank(tankId,500,500,0)
        addTank(1,800,1000,45)
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
        atbuttonbrand.setOnClickListener {
            shot(gunType,tanks[tankId])
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

    //这是一个会自己给自己发信息的无限循环handler
    private var handler = Handler {
        when (it.what) {
            BULLET_SCAN->
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
                when (it.updateBulletPosition()) {
                    TOOLS.CrashType.NoCrash -> {

                    }
                    TOOLS.CrashType.Border -> {
                        it.bulletLiving = false
                    }
                    TOOLS.CrashType.Tank -> {
                        it.bulletLiving = false
                    }
                    else -> {}
                }
            }
            handler.sendEmptyMessageDelayed(BULLET_SCAN,5)
        }
    }

    //添加坦克信息，并初始化View
    private fun addTank(tankId: Int,x:Int,y:Int,direction:Int)
    {
        tanks.add(Tank(tankId,x,y,direction,getViewById(R.layout.tank),getViewById(R.layout.tankbarrel)))//Tank创建
        tanks[tankId].initInfoAndView(world)
    }

    private fun getViewById(ViewId:Int): View {
        return layoutInflater.inflate(ViewId,world,false)
    }


}