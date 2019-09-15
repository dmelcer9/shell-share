package net.danielmelcer.shellshare

import java.awt.Color
import java.io.Reader
import java.util.*

class ANSIReader(val reader:Scanner){

    private val windowSizeCallbacks:MutableList<(ANSIWindowSizeResp)->Unit> = LinkedList();

    public fun addWindowSizeCallback(cb:(ANSIWindowSizeResp)->Unit){
        windowSizeCallbacks.add(cb)
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
                    if(reader.hasNextInt()){
                        val n = nextInt();
                        val c = next();
                        if(c == ';'){
                            if(reader.hasNextInt()){
                                val m = nextInt()
                                val d = next();
                                if(d == 'R'){
                                    callAll(windowSizeCallbacks, ANSIWindowSizeResp(n, m))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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