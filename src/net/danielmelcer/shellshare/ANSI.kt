package net.danielmelcer.shellshare

import java.awt.Color


abstract class ANSI {}

class ANSIGoTo(val x: Int, val y: Int) : ANSI() {
    public override fun toString(): String {
        return "$CSI${y + 1};${x + 1}H"
    }
}

class ANSIBiColor(val upper: Color, val lower: Color) : ANSI() {

    constructor(upper: Int, lower: Int): this(Color(upper), Color(lower))

    override fun toString(): String {
        return CSI + "38;2;" + colorToANSIColor(upper) + "m" + CSI + "48;2;" + colorToANSIColor(lower) + "m\u2580"
    }
}

fun colorToANSIColor(c: Color): String = "${c.red};${c.green};${c.blue}"

const val CSI = "\u001B["