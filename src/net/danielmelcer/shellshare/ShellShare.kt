@file:JvmName("ShellShare")

package net.danielmelcer.shellshare

import java.awt.*
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

    val scanner: Scanner = Scanner(System.`in`)
    val ansiParser = ANSIReader(scanner)
    ansiParser.addWindowSizeCallback { cb ->
        screenDim = Dimension(cb.cols, cb.rows)
    }

    ansiParser.addKeyPressCallback { ch ->

        if(Character.isAlphabetic(ch.toInt())){
            if (Character.isUpperCase(ch)) {
                r.keyPress(KeyEvent.VK_SHIFT)
            }
            r.keyPress(Character.toUpperCase(ch).toInt())
            r.keyRelease(Character.toUpperCase(ch).toInt())

            if (Character.isUpperCase(ch)) {
                r.keyRelease(KeyEvent.VK_SHIFT)
            }
        } else {
            val ks = KeyStroke.getKeyStroke(ch);

            r.keyPress(ks.keyCode)
            r.keyRelease(ks.keyCode)
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

