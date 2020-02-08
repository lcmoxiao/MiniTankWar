package com.example.minitankwar.gameInfo.gamerole.buttle

import android.view.View
import android.widget.FrameLayout
import com.example.minitankwar.CrashDetector
import com.example.minitankwar.R
import com.example.minitankwar.TOOLS
import com.example.minitankwar.TOOLS.dp13
import com.example.minitankwar.TOOLS.dp5
import com.example.minitankwar.gameInfo.gamerole.Bullet
import com.example.minitankwar.gameInfo.gamerole.Tank

class LaserGun(override val bulletView: View,override val shotTankId: Int) : Bullet(dp13, dp5)
{
    override val speed = 10
    override val damage = 1



    override fun setShotPosition(tank: Tank, diffRotation: Int) {
        copyPositionData(tank.getCenterX()-15,tank.getCenterY()-15,tank.getBarrelDirection()+diffRotation)
    }

}