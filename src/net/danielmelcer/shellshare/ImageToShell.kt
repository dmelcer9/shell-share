package net.danielmelcer.shellshare

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.image.BufferedImage
import javax.swing.JOptionPane
import kotlin.math.abs

fun imageToShell(im: BufferedImage, d: Dimension): String {
    val doubleHighDimension = Dimension(d.width, d.height * 2)
    val resizedImage = resizeImageWithAspectRatio(im, doubleHighDimension)

    val sb = StringBuilder()


    for (y in 0 until d.height) {
        val yUpper = y * 2
        val yLower = yUpper + 1

        sb.append(ANSIGoTo(0, y))
        for (x in 0 until d.width) {
            sb.append(ANSIBiColor(resizedImage.getRGB(x, yUpper), resizedImage.getRGB(x, yLower)))
        }
    }

    sb.append(ANSISetBG(Color.BLACK))
    sb.append(ANSISetBG(Color.WHITE))

    return sb.toString()
}

var corner = Point(0,0)
var scaleFactorX = 1.0
var scaleFactorY = 1.0

fun getMouseClickCoordinate(x:Int, y:Int):Point{
    val doubleY = y*2 // Double high characters
    val relX = x- corner.x
    val relY = doubleY - corner.y
    val scaledX = relX * scaleFactorX
    val scaledY = relY * scaleFactorY
    JOptionPane.showMessageDialog(null, "corner:$corner, scalex:${scaleFactorX},$scaledX, scaley:$scaleFactorY, $scaledY");
    return Point(scaledX.toInt(), scaledY.toInt())
}

fun resizeImageWithAspectRatio(im: BufferedImage, d: Dimension): BufferedImage {
    val sourceAspectRatio = im.width.toDouble() / im.height;
    val destAspectRatio = d.getWidth() / d.getHeight();

    val dest: BufferedImage = BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
    val destgraphics = dest.createGraphics()
    destgraphics.background = Color.BLACK
    //destgraphics.dispose()

    val destimageSubRegion: BufferedImage = when {
        abs(sourceAspectRatio - destAspectRatio) < 0.02 -> // Just scale, it's pretty close
            dest
        sourceAspectRatio > destAspectRatio -> {
            // Solid bars on top and bottom
            val subregionHeight = dest.width / sourceAspectRatio;
            val amountOnTop = (dest.height - subregionHeight) / 2
            corner = Point(0, amountOnTop.toInt())
            dest.getSubimage(0, amountOnTop.toInt(), dest.width, subregionHeight.toInt())
        }
        else -> {
            // Solid bars on left and right
            val subregionWidth = dest.height * sourceAspectRatio;
            val amountOnLeft = (dest.width - subregionWidth) / 2
            corner = Point(amountOnLeft.toInt(), 0)
            dest.getSubimage(amountOnLeft.toInt(), 0, subregionWidth.toInt(), dest.height)
        }
    }

    scaleFactorX = im.width.toDouble() / dest.width
    scaleFactorY = im.height.toDouble() / dest.height

    resizeImage(im, destimageSubRegion)
    return dest;
}

fun resizeImage(source: BufferedImage, dest: BufferedImage) {
    val graphics2D: Graphics2D = dest.createGraphics();
    graphics2D.scale(dest.width.toDouble() / source.width, dest.height.toDouble() / source.height);
    graphics2D.drawImage(source, 0, 0, null);
    //graphics2D.dispose()
}

fun resizeImage(source: BufferedImage, d: Dimension): BufferedImage {
    val dest: BufferedImage = BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
    resizeImage(source, dest);
    return dest;
}
