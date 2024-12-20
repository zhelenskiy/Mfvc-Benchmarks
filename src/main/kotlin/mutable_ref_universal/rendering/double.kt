@file:Suppress("NOTHING_TO_INLINE")

package mutable_ref_universal.rendering

import shared.AbstractGraphics
import shared.OffsetInSeconds
import shared.MicrobenchmarkRotations
import shared.asAbstract
import mutable_ref_universal.MutableMfvcWrapper
import shared.screenHeight
import shared.screenWidth
import shared.worldMap
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import java.text.DecimalFormat
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.Timer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

inline operator fun Array<IntArray>.get(location: LocationD): Int = this[location.x][location.y]

inline fun canMove(point: Point2d): Boolean = worldMap[point.x.toInt()][point.y.toInt()] == 0

@JvmInline
value class Point2d(val x: Double, val y: Double) {
    fun plus(vector: Vector2d, wrapper: MutableMfvcWrapper) {
        val result = Point2d(x + vector.x, y + vector.y)
        wrapper.encodeLong0(result.x)
        wrapper.encodeLong1(result.y)
    }

    fun minus(vector: Vector2d, wrapper: MutableMfvcWrapper) {
        val result = Point2d(x - vector.x, y - vector.y)
        wrapper.encodeLong0(result.x)
        wrapper.encodeLong1(result.y)
    }

    fun toLocation(wrapper: MutableMfvcWrapper) {
        val result = LocationD(x.toInt(), y.toInt())
        wrapper.encodeLong0(result.x)
        wrapper.encodeLong1(result.y)
    }

    fun toVector(wrapper: MutableMfvcWrapper) {
        val result = Vector2d(x, y)
        wrapper.encodeLong0(result.x)
        wrapper.encodeLong1(result.y)
    }
}

@Suppress("UnusedReceiverParameter")
inline fun Unit.decodePoint2d(wrapper: MutableMfvcWrapper) = Point2d(wrapper.decodeDouble0(), wrapper.decodeDouble1())
@Suppress("UnusedReceiverParameter")
inline fun Unit.decodeVector2d(wrapper: MutableMfvcWrapper) = Vector2d(wrapper.decodeDouble0(), wrapper.decodeDouble1())
@Suppress("UnusedReceiverParameter")
inline fun Unit.decodeLocationD(wrapper: MutableMfvcWrapper) = LocationD(wrapper.decodeInt0(), wrapper.decodeInt1())

@JvmInline
value class Vector2d(val x: Double, val y: Double) {
    fun rotate(angle: Double, wrapper: MutableMfvcWrapper) {
        val res = Vector2d(x * cos(angle) - y * sin(angle), x * sin(angle) + y * cos(angle))
        wrapper.encodeLong0(res.x)
        wrapper.encodeLong1(res.y)
    }

    fun times(factor: Double, wrapper: MutableMfvcWrapper) {
        val res = Vector2d(x * factor, y * factor)
        wrapper.encodeLong0(res.x)
        wrapper.encodeLong1(res.y)
    }

    fun plus(vector: Vector2d, wrapper: MutableMfvcWrapper) {
        val res = Vector2d(x + vector.x, y + vector.y)
        wrapper.encodeLong0(res.x)
        wrapper.encodeLong1(res.y)
    }

    fun minus(vector: Vector2d, wrapper: MutableMfvcWrapper) {
        val res = Vector2d(x - vector.x, y - vector.y)
        wrapper.encodeLong0(res.x)
        wrapper.encodeLong1(res.y)
    }

    fun abs(wrapper: MutableMfvcWrapper) {
        val res = Vector2d(abs(x), abs(y))
        wrapper.encodeLong0(res.x)
        wrapper.encodeLong1(res.y)
    }

    fun xProjection(wrapper: MutableMfvcWrapper) {
        val res = Vector2d(x, 0.0)
        wrapper.encodeLong0(res.x)
        wrapper.encodeLong1(res.y)
    }

    fun yProjection(wrapper: MutableMfvcWrapper) {
        val res = Vector2d(0.0, y)
        wrapper.encodeLong0(res.x)
        wrapper.encodeLong1(res.y)
    }
}

@JvmInline
value class LocationD(val x: Int, val y: Int) {
    fun toVector(wrapper: MutableMfvcWrapper) {
        val res = Vector2d(x.toDouble(), y.toDouble())
        wrapper.encodeLong0(res.x)
        wrapper.encodeLong1(res.y)
    }

    fun step(vector: Vector2d, wrapper: MutableMfvcWrapper) {
        val res = LocationD(
            (toVector(wrapper).decodeVector2d(wrapper).plus(vector, wrapper).decodeVector2d(wrapper)).x.toInt(),
            (toVector(wrapper).decodeVector2d(wrapper).plus(vector, wrapper).decodeVector2d(wrapper)).y.toInt()
        )
        wrapper.encodeLong0(res.x)
        wrapper.encodeLong1(res.y)
    }
}

fun Double.div(vector: Vector2d, wrapper: MutableMfvcWrapper) {
    val res = Vector2d(this / vector.x, this / vector.y)
    wrapper.encodeLong0(res.x)
    wrapper.encodeLong1(res.y)
}

class MyPanelD : JPanel(), KeyListener, MouseListener {
    private var startTime: Long = System.nanoTime()
    private var frameCount = 0
    private var fps = 100.0
    private var minFps = 100.0
    private val veryStartTime = System.nanoTime()

    var pos = Point2d(22.0, 12.0) // start position

    var dir = Vector2d(-1.0, 0.0) // direction vector

    var plane = Vector2d(0.0, 0.66) //the 2d raycaster version of camera plane
 
    init {
        addKeyListener(this)
        addMouseListener(this)
        isFocusable = true

        // Set up the timer to update the FPS every second
        Timer(1000) {
            // Calculate the FPS and update the label text
            val currentTime = System.nanoTime()
            val elapsedTime = (currentTime - startTime) / 1e9
            fps = frameCount / elapsedTime
            if ((currentTime - veryStartTime) / 1e9 > OffsetInSeconds) {
                minFps = minFps.coerceAtMost(fps)
            }
            startTime = currentTime
            frameCount = 0
        }.start()
        startTime = System.nanoTime()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g as Graphics2D

        mainLoop(g.asAbstract())

        g.color = Color.BLACK
        g.drawString("Current FPS: ${DecimalFormat("#.#").format(fps)}, Min FPS: ${DecimalFormat("#.#").format(minFps)}", 10, 20)

        frameCount++
    }

    private fun mainLoop(g: AbstractGraphics) {
        repeat(100) {
            drawScene(pos, dir, plane, g)
        }
    }

    override fun keyPressed(e: KeyEvent) {
        val frameTime = 1 / fps
        //speed modifiers

        //speed modifiers
        val moveSpeed = frameTime * .5 //the constant value is in squares/second
        val rotSpeed = frameTime * .3 //the constant value is in radians/second
        val wrapper = MutableMfvcWrapper()
        when (e.keyCode) {
            KeyEvent.VK_UP -> {
                if(canMove((pos.plus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)), wrapper)).decodePoint2d(wrapper))) {
                    pos = (pos.plus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).xProjection(wrapper).decodeVector2d(wrapper), wrapper)).decodePoint2d(wrapper)
                }
                if(canMove((pos.plus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper), wrapper)).decodePoint2d(wrapper))) {
                    pos = pos.plus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper)
                }
            }
            KeyEvent.VK_DOWN -> {
                if(canMove(pos.minus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).xProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper))) {
                    pos = pos.minus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).xProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper)
                }
                if(canMove(pos.minus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper))) {
                    pos = pos.minus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper)
                }
            }
            KeyEvent.VK_LEFT -> {
                //both camera direction and camera plane must be rotated
                dir = dir.rotate(rotSpeed, wrapper).decodeVector2d(wrapper)
                plane = plane.rotate(rotSpeed, wrapper).decodeVector2d(wrapper)
            }
            KeyEvent.VK_RIGHT -> {
                //both camera direction and camera plane must be rotated
                dir = dir.rotate(-rotSpeed, wrapper).decodeVector2d(wrapper)
                plane = plane.rotate(-rotSpeed, wrapper).decodeVector2d(wrapper)
            }
        }
        repaint()
    }

    override fun mouseClicked(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
    override fun mousePressed(e: MouseEvent) {}
    override fun mouseReleased(e: MouseEvent) {}
    override fun keyTyped(e: KeyEvent) {}
    override fun keyReleased(e: KeyEvent) {}
}

fun heavyActionDouble(graphics: AbstractGraphics) {
    var pos = Point2d(22.0, 12.0)
    var dir = Vector2d(-1.0, 0.0)
    var plane = Vector2d(0.0, 0.66)
    val fps = 10.0
    val frameTime = 1 / fps
    //speed modifiers

    val wrapper = MutableMfvcWrapper()
    //speed modifiers
    val moveSpeed = frameTime * .5 //the constant value is in squares/second
    val rotSpeed = frameTime * .3 //the constant value is in radians/second
    repeat(MicrobenchmarkRotations) {
        drawScene(pos, dir, plane, graphics)
        if(canMove((pos.plus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)), wrapper)).decodePoint2d(wrapper))) {
            pos = (pos.plus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).xProjection(wrapper).decodeVector2d(wrapper), wrapper)).decodePoint2d(wrapper)
        }
        if(canMove((pos.plus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper), wrapper)).decodePoint2d(wrapper))) {
            pos = pos.plus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper)
        }
        drawScene(pos, dir, plane, graphics)
        if(canMove(pos.minus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).xProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper))) {
            pos = pos.minus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).xProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper)
        }
        if(canMove(pos.minus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper))) {
            pos = pos.minus((dir.times(moveSpeed, wrapper).decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper), wrapper).decodePoint2d(wrapper)
        }
        drawScene(pos, dir, plane, graphics)
        dir = dir.rotate(rotSpeed, wrapper).decodeVector2d(wrapper)
        plane = plane.rotate(rotSpeed, wrapper).decodeVector2d(wrapper)
        drawScene(pos, dir, plane, graphics)
    }
}

private fun drawScene(pos: Point2d, dir: Vector2d, plane: Vector2d, g: AbstractGraphics) {
    val wrapper = MutableMfvcWrapper()
    for (x in 0 until screenWidth) {
        //calculate ray position and direction
        val cameraX = 2 * x / screenHeight.toDouble() - 1 //x-coordinate in camera space
        val rayDir =
            dir.plus(plane.times(cameraX, wrapper).decodeVector2d(wrapper), wrapper).decodeVector2d(wrapper)
        //which box of the map we're in
        var mapLocation = pos.toLocation(wrapper).decodeLocationD(wrapper)

        //length of ray from current position to next x or y-side
        var sideDist: Vector2d

        //length of ray from one x or y-side to next x or y-side
        //these are derived as:
        //deltaDistX = sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX))
        //deltaDistY = sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY))
        //which can be simplified to abs(|rayDir| / rayDirX) and abs(|rayDir| / rayDirY)
        //where |rayDir| is the length of the vector (rayDirX, rayDirY). Its length,
        //unlike (dirX, dirY) is not 1, however this does not matter, only the
        //ratio between deltaDistX and deltaDistY matters, due to the way the DDA
        //stepping further below works. So the values can be computed as below.
        // Division through zero is prevented, even though technically that's not
        // needed in C++ with IEEE 754 floating point values.
        val deltaDist = 1.0.div(rayDir.abs(wrapper).decodeVector2d(wrapper), wrapper).decodeVector2d(wrapper)

        var perpWallDist: Double

        //what direction to step in x or y-direction (either +1 or -1)
        var step: Vector2d

        var hit = 0 //was there a wall hit?
        var side = 0 //was a NS or a EW wall hit?
        //calculate step and initial sideDist
        if (rayDir.x < 0) {
            step = Vector2d((-1).toDouble(), 0.0)
            sideDist = (pos.toVector(wrapper).decodeVector2d(wrapper)
                .minus(mapLocation.toVector(wrapper).decodeVector2d(wrapper), wrapper)
                .decodeVector2d(wrapper)).xProjection(wrapper).decodeVector2d(wrapper)
                .times(deltaDist.x, wrapper).decodeVector2d(wrapper)
        } else {
            step = Vector2d(1.toDouble(), 0.0)
            sideDist = (mapLocation.toVector(wrapper).decodeVector2d(wrapper).plus(Vector2d(1.0, 0.0), wrapper)
                .decodeVector2d(wrapper)
                .minus(pos.toVector(wrapper).decodeVector2d(wrapper), wrapper).decodeVector2d(wrapper)).xProjection(
                    wrapper
                ).decodeVector2d(wrapper)
                .times(deltaDist.x, wrapper).decodeVector2d(wrapper)
        }
        if (rayDir.y < 0) {
            step = step.plus(Vector2d(0.0, (-1).toDouble()), wrapper).decodeVector2d(wrapper)
            sideDist = sideDist.plus(
                (pos.toVector(wrapper).decodeVector2d(wrapper)
                    .minus(mapLocation.toVector(wrapper).decodeVector2d(wrapper), wrapper)
                    .decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper)
                    .times(deltaDist.y, wrapper).decodeVector2d(wrapper), wrapper
            ).decodeVector2d(wrapper)
        } else {
            step = step.plus(Vector2d(0.0, 1.toDouble()), wrapper).decodeVector2d(wrapper)
            sideDist = sideDist.plus(
                (mapLocation.toVector(wrapper).decodeVector2d(wrapper).plus(Vector2d(0.0, 1.0), wrapper)
                    .decodeVector2d(wrapper)
                    .minus(pos.toVector(wrapper).decodeVector2d(wrapper), wrapper)
                    .decodeVector2d(wrapper)).yProjection(wrapper).decodeVector2d(wrapper)
                    .times(deltaDist.y, wrapper).decodeVector2d(wrapper), wrapper
            ).decodeVector2d(wrapper)
        }
        //perform DDA
        while (hit == 0) {
            //jump to next map square, either in x-direction, or in y-direction
            if (sideDist.x < sideDist.y) {
                sideDist = sideDist.plus(deltaDist.xProjection(wrapper).decodeVector2d(wrapper), wrapper)
                    .decodeVector2d(wrapper)
                mapLocation = mapLocation.step(step.xProjection(wrapper).decodeVector2d(wrapper), wrapper)
                    .decodeLocationD(wrapper)
                side = 0
            } else {
                sideDist = sideDist.plus(deltaDist.yProjection(wrapper).decodeVector2d(wrapper), wrapper)
                    .decodeVector2d(wrapper)
                mapLocation = mapLocation.step(step.yProjection(wrapper).decodeVector2d(wrapper), wrapper)
                    .decodeLocationD(wrapper)
                side = 1
            }
            //Check if ray has hit a wall
            if (worldMap[mapLocation] > 0) hit = 1
        }
        //Calculate distance projected on camera direction. This is the shortest distance from the point where the wall is
        //hit to the camera plane. Euclidean to center camera point would give fisheye effect!
        //This can be computed as (mapX - posX + (1 - stepX) / 2) / rayDirX for side == 0, or same formula with Y
        //for size == 1, but can be simplified to the code below thanks to how sideDist and deltaDist are computed:
        //because they were left scaled to |rayDir|. sideDist is the entire length of the ray above after the multiple
        //steps, but we subtract deltaDist once because one step more into the wall was taken above.
        perpWallDist =
            if (side == 0) (sideDist.minus(deltaDist, wrapper).decodeVector2d(wrapper)).x
            else (sideDist.minus(deltaDist, wrapper).decodeVector2d(wrapper)).y

        //Calculate height of line to draw on screen
        val lineHeight = (screenHeight / perpWallDist).toInt()

        //calculate lowest and highest pixel to fill in current stripe
        val drawStart = (-lineHeight / 2 + screenHeight / 2).coerceAtLeast(0)
        val drawEnd = (lineHeight / 2 + screenHeight / 2).coerceAtMost(screenHeight - 1)

        //choose wall color
        var color =
            when (worldMap[mapLocation]) {
                1 -> 0xFF0000 //red
                2 -> 0x00FF00 //green
                3 -> 0x00FF00 //blue
                4 -> 0x0000FF //white
                else -> 0xFFFF00 //yellow
            }

        //give x and y sides different brightness
        if (side == 1) {
            color /= 2
        }

        //draw the pixels of the stripe as a vertical line
        g.setIntColor(color)
        g.drawLine(x, drawStart, x, drawEnd)
    }
}

class MyFrameD : JFrame() {
    init {
        add(MyPanelD())
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(screenWidth, screenHeight)
        isVisible = true

        // Set up the game loop
        Thread {
            while (true) {
                // Repaint the panel and sleep for a short time
                SwingUtilities.invokeLater {
                    contentPane.repaint()
                }
                Thread.sleep(10)
            }
        }.start()
    }
}

fun main() {
    MyFrameD()
}
