package com.example.minitankwar.gameInfo.gamerole.buttle

import com.example.minitankwar.TOOLS.dp13
import com.example.minitankwar.TOOLS.dp5
import com.example.minitankwar.baseinterface.IBaseView2D
import com.example.minitankwar.gameInfo.gamerole.Tank

class LaserGun(shotTankId: Int) : Bullet(dp13, dp5,shotTankId)
{


    override val loadInterval = 200.toLong()
    override val speed = 10.0
    override val damage = 1
    override val type = 3
    override fun setShotPosition(tank: Tank, diffRotation: Int) {
        shape.copyPosAndDir(tank.shape.cPoint.x,tank.shape.cPoint.y,tank.getBarrelDirection()+diffRotation)
    }

}