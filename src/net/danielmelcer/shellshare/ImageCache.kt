package net.danielmelcer.shellshare

import java.awt.Dimension
import java.awt.image.BufferedImage
import java.util.*

class ImageCache<T> constructor(val generator: (BufferedImage, Dimension) -> T) {

    private var fullImage: BufferedImage? = null;
    private val cachedSizes: HashMap<Dimension, T> = HashMap();

    fun getResult(d: Dimension): T? {
        return if (this.fullImage != null) {
            cachedSizes.computeIfAbsent(d) { dimension: Dimension ->
                this.generator(
                    this.fullImage!!,
                    dimension
                )
            }
        } else {
            null;
        }
    }

    fun setImage(image:BufferedImage){
        this.fullImage = image;
        cachedSizes.clear();
    }

}