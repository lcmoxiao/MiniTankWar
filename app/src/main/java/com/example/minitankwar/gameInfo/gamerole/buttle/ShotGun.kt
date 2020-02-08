package com.example.minitankwar.gameInfo.gamerole.buttle

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS
import com.example.minitankwar.gameInfo.gamerole.Bullet
import com.example.minitankwar.gameInfo.gamerole.Tank

class ShotGun(override val bulletView: View,override val shotTankId: Int) : Bullet(TOOLS.dp13, TOOLS.dp5)
{
    override val speed = 7
    override val damage = 2



    override fun setShotPosition(tank: Tank, diffRotation: Int) {
        copyPositionData(tank.getCenterX()-15,tank.getCenterY()-15,tank.getBarrelDirection()+diffRotation)
    }



}