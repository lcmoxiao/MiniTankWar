package com.example.minitankwar.gameInfo.gamerole.buttle

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS
import com.example.minitankwar.gameInfo.gamerole.Bullet
import com.example.minitankwar.gameInfo.gamerole.Tank

class RocketGun(override val bulletView: View,override val shotTankId: Int) : Bullet(2*TOOLS.dp13, TOOLS.dp10)
{

    override val speed = 5
    override val damage = 3

    override fun setShotPosition(tank: Tank, diffRotation: Int) {
        copyPositionData(tank.getCenterX()-25,tank.getCenterY()-25,tank.getBarrelDirection()+diffRotation)
    }

    fun boomcrash(){

    }



}