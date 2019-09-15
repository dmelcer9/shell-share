@file:JvmName("ShellShare")

package net.danielmelcer.shellshare

import java.awt.*
import java.awt.event.InputEvent
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.util.*
import java.awt.event.KeyEvent
import javax.swing.KeyStroke


var screenDim = Dimension(100,100)


fun main(args: Array<String>) {
    //val serverSocket : ServerSocket = ServerSocket(8000);
    val r = Robot()
    //println("Hello")
        //val socket: Socket = serverSocket.accept();

    println(ANSIMouseEnable())

    val scanner: Scanner = Scanner(System.`in`)
    val ansiParser = ANSIReader(scanner)
    ansiParser.addWindowSizeCallback { cb ->
        screenDim = Dimension(cb.cols, cb.rows)
    }

    ansiParser.addKeyPressCallback { ch ->

        if(Character.isLetterOrDigit(ch) || Character.isWhitespace(ch)){
            if (Character.isUpperCase(ch)) {
                r.keyPress(KeyEvent.VK_SHIFT)
            }
            r.keyPress(Character.toUpperCase(ch).toInt())
            r.keyRelease(Character.toUpperCase(ch).toInt())

            if (Character.isUpperCase(ch)) {
                r.keyRelease(KeyEvent.VK_SHIFT)
            }
        } else {
            val ks = KeyEvent.getExtendedKeyCodeForChar(ch.toInt());

            r.keyPress(ks)
            r.keyRelease(ks)
        }
    }

    ansiParser.addKeyCodeCallback { kc ->
        r.keyPress(kc)
        r.keyRelease(kc)
    }

    ansiParser.addMouseCallback { cb ->
        val buttons = (cb.modifier and 0x04)

        val buttonPressed = when(buttons){
            0 -> InputEvent.BUTTON1_DOWN_MASK
            1 -> InputEvent.BUTTON2_DOWN_MASK
            2 -> InputEvent.BUTTON3_DOWN_MASK
            else -> (InputEvent.BUTTON1_DOWN_MASK or InputEvent.BUTTON2_DOWN_MASK or InputEvent.BUTTON3_DOWN_MASK)
        }

        val point = getMouseClickCoordinate(cb.x, cb.y)
        r.mouseMove(point.x, point.y)

        if(buttonPressed != 3){
            r.mousePress(buttonPressed)
        } else{
            r.mouseRelease(buttonPressed)
        }

    }

    val t = Thread {
        ansiParser.startParsing()
    }
    t.start()

    while(true){

        val capture = Rectangle(Toolkit.getDefaultToolkit().screenSize)
        val im = r.createScreenCapture(capture)
        val bw : BufferedWriter = BufferedWriter(OutputStreamWriter(System.out), 10000000)
        val imstr = imageToShell(im, screenDim)
        bw.write(imstr)
        bw.flush()

        bw.write(ANSIWindowSizeQuery().toString())
        bw.flush()
        //Thread.sleep(1000)
    }
}

