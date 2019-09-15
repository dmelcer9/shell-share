@file:JvmName("ShellShare")

package net.danielmelcer.shellshare

import java.awt.*
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.util.*

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
        r.keyPress(ch.toInt())
        r.keyRelease(ch.toInt())
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

