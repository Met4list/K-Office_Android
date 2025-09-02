package com.k_office.app.application.config

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import com.k_office.app.BuildConfig
import com.k_office.data.provider.BaseConfigProvider
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.inject.Inject

class BaseConfigProviderImpl @Inject constructor(): BaseConfigProvider {

    override fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    override fun provideIsDevEnv(): Boolean {
        return BuildConfig.IS_DEV
    }

    override fun provideIsDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    @SuppressLint("PackageManagerGetSignatures", "TimberArgCount")
    override fun provideAppSignature(context: Context): String? {
        return try {
            val packageName = context.packageName
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = context.packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNING_CERTIFICATES
                )
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = context.packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES
                )
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            signatures?.forEach { signature ->
                val signatureBytes = signature.toByteArray()
                val message = "$packageName ${Base64.encodeToString(signatureBytes, Base64.NO_WRAP)}"

                val messageDigest = MessageDigest.getInstance("SHA-256")
                messageDigest.update(message.toByteArray(StandardCharsets.UTF_8))
                val hashSignature = messageDigest.digest()

                val truncated = hashSignature.copyOfRange(0, 9)

                return Base64.encodeToString(truncated, Base64.NO_PADDING or Base64.NO_WRAP)
                    .substring(0, 11)
            }
            null
        } catch (e: Exception) {
            Timber.e("AppSignatureHelper", "Error generating hash: ${e.message}", e)
            null
        }
    }
}