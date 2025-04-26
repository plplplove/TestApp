package com.example.testapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class BackgroundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.background)
    private val cloudBitmap = BitmapFactory.decodeResource(resources, R.drawable.cloud)

    private val clouds = mutableListOf<Cloud>()

    init {
        clouds.add(createCloud())
        clouds.add(createCloud())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(backgroundBitmap, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), null)

        updateClouds()
        for (cloud in clouds) {
            canvas.drawBitmap(cloud.bitmap, cloud.x, cloud.y, null)
        }

        postInvalidateOnAnimation()
    }

    private fun updateClouds() {
        for (cloud in clouds) {
            cloud.x += cloud.speedX

            if (cloud.x > width) {
                cloud.x = -cloud.bitmap.width.toFloat()
                cloud.y = Random.nextFloat() * height * 0.3f
            }
        }
    }

    private fun createCloud(): Cloud {
        val random = Random.nextInt(2)

        val sizeMultiplier = if (random == 0) {
            0.45f + Random.nextFloat() * 0.1f
        } else {
            0.2f + Random.nextFloat() * 0.1f
        }

        val scaledCloud = Bitmap.createScaledBitmap(
            cloudBitmap,
            (cloudBitmap.width * sizeMultiplier).toInt(),
            (cloudBitmap.height * sizeMultiplier).toInt(),
            true
        )

        return Cloud(
            x = Random.nextFloat() * width,
            y = Random.nextFloat() * height * 0.3f,
            speedX = 0.5f + Random.nextFloat(),
            bitmap = scaledCloud
        )
    }

    data class Cloud(
        var x: Float,
        var y: Float,
        var speedX: Float,
        val bitmap: Bitmap
    )
}