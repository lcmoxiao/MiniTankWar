package com.example.minitankwar


import com.example.minitankwar.TOOLS.dp10
import com.example.minitankwar.TOOLS.dp410
import com.example.minitankwar.gameInfo.gamerole.Bullet
import com.example.minitankwar.gameInfo.gamerole.Tank
import com.example.minitankwar.gameInfo.gamerole.Wall


//包括所有的元素集合
//和碰撞判定函数
class CrashDetector{
    companion object{
        var tanks:ArrayList<Tank> = ArrayList()
        var bullets:ArrayList<Bullet> = ArrayList()
        var walls:ArrayList<Wall> = ArrayList()

        private fun lineIsOnLine(obj1:Object2D,obj2:Object2D,obj3:Object2D,obj4:Object2D):Boolean{
             return TOOLS.lineIsOnLine(obj1.x,obj1.y,obj2.x,obj2.y,obj3.x,obj3.y,obj4.x,obj4.y)
        }

        fun crashDetect(object2D: Object2D): TOOLS.CrashType
        {
            if(crashTank(object2D))
            {
                return TOOLS.CrashType.Tank
            }
            if(crashWall(object2D))
            {
                return TOOLS.CrashType.Wall
            }
            if(crashBorder(object2D)) {
                return TOOLS.CrashType.Border
            }
            return TOOLS.CrashType.NoCrash
        }

        //判断该Position会不会和边界碰撞 (坦克||子弹)
        private fun crashBorder(object2D: Object2D):Boolean{
            object2D.shape.getABCD(object2D.direction).forEach { if(it.x<dp10||it.x>(1080-dp10)||it.y<dp10||it.y>dp410)return true }
            return false
        }

        private fun crashWall(object2D: Object2D):Boolean{
            var wallId = 0
            while(wallId< walls.size)
            {
                val shape = walls[wallId].shape
                val direction =  walls[wallId].direction
                val coordinates = shape.getLimitCoordinates(direction)
                val wallABCD = shape.getOrderABCD(direction)
                val bulletABCD = object2D.shape.getOrderABCD(object2D.direction)
                bulletABCD.forEach{ position->
                    //判断是否在最大范围内
                    if (position.x<coordinates[0].x&&position.x>coordinates[2].x&&position.y<coordinates[1].y&&position.y>coordinates[3].y)
                    {
                        var i1 = 0
                        var j1 = 1
                        var i2: Int
                        var j2: Int
                        while(i1<4){
                            i2 = 0
                            j2 = 1
                            while(i2<4){
                                if(lineIsOnLine(bulletABCD[i2],bulletABCD[j2],wallABCD[i1],wallABCD[j1]))return true
                                i2++
                                j2++
                                if(j2==4)j2=0
                            }
                            i1++
                            j1++
                            if(j1==4)j1=0
                        }
                    }
                }
                wallId++
            }
            return false
        }

        private fun crashTank(object2D: Object2D):Boolean{
            var tankId = 0
            while(tankId<tanks.size)
            {
                val shape = tanks[tankId].shape
                val direction = tanks[tankId].direction
                val coordinates = shape.getLimitCoordinates(direction)
                if(object2D is Bullet && object2D.shotTankId ==  tankId) {
                    tankId++
                    continue//子弹不打自己
                }
                val tankABCD = shape.getOrderABCD(direction)
                val bulletABCD = object2D.shape.getOrderABCD(object2D.direction)
                bulletABCD.forEach{ position->
                    //判断是否在最大范围内
                    if (position.x<coordinates[0].x&&position.x>coordinates[2].x&&position.y<coordinates[1].y&&position.y>coordinates[3].y)
                    {
                        var i1 = 0
                        var j1 = 1
                        var i2: Int
                        var j2: Int
                        while(i1<4){
                            i2 = 0
                            j2 = 1
                            while(i2<4){
                                if(lineIsOnLine(bulletABCD[i2],bulletABCD[j2],tankABCD[i1],tankABCD[j1]))return true
                                i2++
                                j2++
                                if(j2==4)j2=0
                            }
                            i1++
                            j1++
                            if(j1==4)j1=0
                        }
                    }
                }
                tankId++
            }
            return false
        }
    }
}