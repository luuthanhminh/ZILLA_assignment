package com.zilla.android.util

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange

object TimeUtils {

    @SuppressLint("DefaultLocale")
    fun formatDuration(duration: Int): String {
        var duration = duration
        duration /= 1000 // milliseconds into seconds
        var minute = duration / 60
        val hour = minute / 60
        minute %= 60
        val second = duration % 60
        return if (hour != 0)
            String.format("%2d:%02d:%02d", hour, minute, second)
        else
            String.format("%02d:%02d", minute, second)
    }


    fun create(@ColorInt startColor: Int, @ColorInt endColor: Int, radius: Int,
               @FloatRange(from = 0.0, to = 1.0) centerX: Float,
               @FloatRange(from = 0.0, to = 1.0) centerY: Float): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.colors = intArrayOf(startColor, endColor)
        gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
        gradientDrawable.gradientRadius = radius.toFloat()
        gradientDrawable.setGradientCenter(centerX, centerY)
        return gradientDrawable
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
                (bitmap.width / 2).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }
}