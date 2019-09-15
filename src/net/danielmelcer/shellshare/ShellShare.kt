@file:JvmName("ShellShare")

package net.danielmelcer.shellshare

import java.awt.*
import java.io.BufferedWriter
import java.io.OutputStreamWriter


fun main(args: Array<String>) {
    //val serverSocket : ServerSocket = ServerSocket(8000);
    val r = Robot()
    //println("Hello")
        //val socket: Socket = serverSocket.accept();
    while(true){
        val capture = Rectangle(Toolkit.getDefaultToolkit().screenSize)
        val im = r.createScreenCapture(capture)
        val bw : BufferedWriter = BufferedWriter(OutputStreamWriter(System.out), 10000000)
        val imstr = imageToShell(im, Dimension(477, 118))
        bw.write(imstr)
        bw.flush()
    }
}

