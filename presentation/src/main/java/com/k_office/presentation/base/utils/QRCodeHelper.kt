package com.k_office.presentation.base.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

object QRCodeHelper {

    fun generateQRCode(content: String?, width: Int = 600, height: Int = 300): Bitmap? {
        if (content.isNullOrEmpty()) return null
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")

        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.CODE_128,
            width,
            height,
            hints
        )

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}