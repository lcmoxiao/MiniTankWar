package com.example.minitankwar.gameInfo.gamerole.buttle

import com.example.minitankwar.TOOLS.dp13
import com.example.minitankwar.TOOLS.dp5
import com.example.minitankwar.gameInfo.gamerole.Bullet
import com.example.minitankwar.gameInfo.gamerole.Tank

class LaserGun(override val shotTankId: Int) : Bullet(dp13, dp5)
{
    override val speed = 10
    override val damage = 1
    override val type = 3
    override fun setShotPosition(tank: Tank, diffRotation: Int) {
        copyPositionData(tank.getCenterX()-15,tank.getCenterY()-15,tank.getBarrelDirection()+diffRotation)
    }

}