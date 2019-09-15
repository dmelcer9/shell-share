@file:JvmName("ShellShare")

package net.danielmelcer.shellshare

import java.awt.*


fun main(args: Array<String>) {
    //val serverSocket : ServerSocket = ServerSocket(8000);
    val r = Robot()
    //println("Hello")
        //val socket: Socket = serverSocket.accept();
    while(true){
        val capture = Rectangle(Toolkit.getDefaultToolkit().screenSize)
        val im = r.createScreenCapture(capture)
        print(imageToShell(im, Dimension(185, 52)))
    }
}

