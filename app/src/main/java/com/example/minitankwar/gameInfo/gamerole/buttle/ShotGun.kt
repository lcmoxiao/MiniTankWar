package com.example.minitankwar.gameInfo.gamerole.buttle


import com.example.minitankwar.TOOLS
import com.example.minitankwar.gameInfo.gamerole.Tank

class ShotGun(shotTankId: Int) : Bullet(TOOLS.dp13, TOOLS.dp5,shotTankId)
{

    override val loadInterval = 300.toLong()
    override val speed = 7.0
    override val damage = 2
    override val type = 1
    override fun setShotPosition(tank: Tank, diffRotation: Int) {
        shape.copyPosAndDir(tank.shape.cPoint.x,tank.shape.cPoint.y,tank.getBarrelDirection()+diffRotation)
    }

}