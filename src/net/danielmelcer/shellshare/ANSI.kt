package net.danielmelcer.shellshare

import java.awt.Color
import java.awt.event.KeyEvent
import java.io.Reader
import java.util.*

class ANSIReader(val reader:Scanner){

    private val windowSizeCallbacks:MutableList<(ANSIWindowSizeResp)->Unit> = LinkedList();
    private val keyPressCallbacks:MutableList<(Char)->Unit> = LinkedList();
    private val keyCodeCallbacks:MutableList<(Int)->Unit> = LinkedList();
    private val mouseCallbacks:MutableList<(ANSIMouseClick)->Unit> = LinkedList();

    private var soFar:String = "";

    public fun addWindowSizeCallback(cb:(ANSIWindowSizeResp)->Unit){
        windowSizeCallbacks.add(cb)
    }

    public fun addKeyPressCallback(cb:(Char)->Unit){
        keyPressCallbacks.add(cb)
    }

    fun addKeyCodeCallback(cb:(Int)->Unit){
        keyCodeCallbacks.add(cb);
    }

    fun addMouseCallback(cb:(ANSIMouseClick)->Unit){
        mouseCallbacks.add(cb)
    }


    private fun next():Char{
        return reader.next()[0];
    }

    private fun nextInt():Int{
        var i:Int = 0
        while(reader.hasNextInt()){
            i *= 10
            i += reader.nextInt()
        }
        return i
    }

    private fun <T> callAll(l:List<(T)->Unit>, t:T):Unit{
        l.forEach {cb -> cb(t)}
    }

    fun startParsing():Nothing{
        reader.useDelimiter("");

        while(true){

            val a = next()

            if(a == '\u001B'){
                val b = next()
                if(b == '['){
                    if(reader.hasNextInt()) {
                        val n = nextInt();
                        val c = next();
                        if (c == ';') {
                            if (reader.hasNextInt()) {
                                val m = nextInt()
                                val d = next();
                                if (d == 'R') {
                                    callAll(windowSizeCallbacks, ANSIWindowSizeResp(n, m))
                                    continue;
                                }
                            }
                        }
                    } else {
                        val c = next();
                        when (c) {
                            'A' -> callAll(keyCodeCallbacks, KeyEvent.VK_UP)
                            'B' -> callAll(keyCodeCallbacks, KeyEvent.VK_DOWN)
                            'C' -> callAll(keyCodeCallbacks, KeyEvent.VK_LEFT)
                            'D' -> callAll(keyCodeCallbacks, KeyEvent.VK_RIGHT)
                            'M' -> {
                                val modifiers = next().toInt()
                                val x = next().toInt() - 32
                                val y = next().toInt() - 32
                                callAll(mouseCallbacks, ANSIMouseClick(modifiers, x, y))
                            }
                        }
                    }
                }
            } else{
                callAll(keyPressCallbacks, a)
                continue;
            }

        }
    }
}

class ANSIMouseEnable(): ANSI(){
    override fun toString(): String {
        return "$CSI?1000h"
    }
}

class ANSIMouseClick(val modifier:Int, val x:Int, val y:Int): ANSI()

class ANSISetFG(val c:Color): ANSI(){
    override fun toString(): String {
        return CSI + "48;2;" + colorToANSIColor(c) + "m"
    }
}

class ANSISetBG(val c:Color): ANSI(){
    override fun toString(): String {
        return CSI + "38;2;" + colorToANSIColor(c) + "m"
    }
}

class ANSIWindowSizeResp(val rows : Int, val cols:Int) : ANSI(){}

class ANSIWindowSizeQuery : ANSI(){
    override fun toString(): String {
        return ANSIGoTo(9999,9999).toString() + "${CSI}6n"
    }
}

abstract class ANSI {}

class ANSIGoTo(val x: Int, val y: Int) : ANSI() {
    public override fun toString(): String {
        return "$CSI${y + 1};${x + 1}H"
    }
}

class ANSIBiColor(val upper: Color, val lower: Color) : ANSI() {

    constructor(upper: Int, lower: Int): this(Color(upper), Color(lower))

    override fun toString(): String {
        return ANSISetFG(upper).toString() + ANSISetBG(lower).toString() + "\u2580"
    }
}

fun colorToANSIColor(c: Color): String = "${c.red};${c.green};${c.blue}"

const val CSI = "\u001B["