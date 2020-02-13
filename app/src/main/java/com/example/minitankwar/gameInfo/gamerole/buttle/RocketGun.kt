package com.example.minitankwar.gameInfo.gamerole.buttle

import com.example.minitankwar.TOOLS
import com.example.minitankwar.gameInfo.gamerole.Tank

class RocketGun(shotTankId: Int) : Bullet(2*TOOLS.dp13, TOOLS.dp10,shotTankId)
{


    override val loadInterval = 400.toLong()
    override val speed = 5.0
    override val damage = 3
    override val type = 2
    override fun setShotPosition(tank: Tank, diffRotation: Int) {
        shape.copyPosAndDir(tank.shape.cPoint.x,tank.shape.cPoint.y,tank.getBarrelDirection()+diffRotation)
    }
    fun boomcrash(){

    }

}