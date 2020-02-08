package com.example.minitankwar.gameInfo.gamerole

import com.example.minitankwar.Object2D
import com.example.minitankwar.Shape
import com.example.minitankwar.TOOLS.dp40

class Wall: Object2D{
    var healthy = 999

    constructor(x:Int,y:Int,direction:Int):super(x,y,direction,40,40)
    constructor(x:Int,y:Int,direction:Int,healthy:Int):super(x,y,direction,40,40)
    {
        this.healthy = healthy
    }
}