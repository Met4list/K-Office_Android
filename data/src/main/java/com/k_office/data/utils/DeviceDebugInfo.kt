package com.k_office.data.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Base64
import androidx.annotation.Keep
import com.google.gson.Gson
import com.k_office.data.provider.BaseConfigProvider
import java.util.Locale
import java.util.TimeZone

@Keep
data class DeviceDebugInfo(
    val appVersionName: String,
    val appVersionCode: Long,
    val flavor: String,
    val buildType: String,
    val buildVariant: String,
    val isDevFlavor: Boolean,
    val packageName: String,
    val installer: String?,
    val androidRelease: String,
    val sdkInt: Int,
    val securityPatch: String?,
    val incremental: String?,
    val previewSdkInt: Int,
    val manufacturer: String,
    val brand: String,
    val model: String,
    val device: String,
    val product: String,
    val hardware: String,
    val board: String,
    val deviceName: String?,
    val abis: List<String>,
    val locale: String,
    val locales: List<String>,
    val timezone: String,
    val screenWidthPx: Int,
    val screenHeightPx: Int,
    val densityDpi: Int,
    val smallestWidthDp: Int,
    val isEmulator: Boolean,
    val totalRamMb: Long?,
)

private val headerGson = Gson()

const val HEADER_BUILD_TYPE = "X-Build-Type"
const val HEADER_FLAVOR = "X-Flavor"
const val HEADER_BUILD_VARIANT = "X-Build-Variant"
const val HEADER_DEVICE_INFO = "X-Device-Info"

fun deviceDebugHeaders(context: Context, config: BaseConfigProvider): Map<String, String> {
    val info = collectDeviceDebugInfo(context, config)
    val json = headerGson.toJson(info)
    // Base64, щоб кирилиця в назві пристрою не зламала HTTP-заголовок
    val encoded = Base64.encodeToString(json.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    return mapOf(
        HEADER_BUILD_TYPE to info.buildType,
        HEADER_FLAVOR to info.flavor.headerSafe(),
        HEADER_BUILD_VARIANT to info.buildVariant.headerSafe(),
        "X-App-Version" to info.appVersionName.headerSafe(),
        "X-App-Version-Code" to info.appVersionCode.toString(),
        "X-Android-Version" to info.androidRelease.headerSafe(),
        "X-Android-Sdk" to info.sdkInt.toString(),
        "X-Device-Manufacturer" to info.manufacturer.headerSafe(),
        "X-Device-Brand" to info.brand.headerSafe(),
        "X-Device-Model" to info.model.headerSafe(),
        "X-Device-Name" to (info.deviceName ?: "").headerSafe(),
        "X-Installer" to (info.installer ?: "unknown").headerSafe(),
        "X-Abi" to info.abis.joinToString(",").headerSafe(),
        "X-Locale" to info.locale.headerSafe(),
        "User-Agent" to buildUserAgent(info),
        HEADER_DEVICE_INFO to encoded,
    )
}

fun collectDeviceDebugInfo(context: Context, config: BaseConfigProvider): DeviceDebugInfo {
    val packageInfo = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }.getOrNull()
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo?.longVersionCode ?: 0L
    } else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toLong() ?: 0L
    }
    val metrics = context.resources.displayMetrics
    val locales = currentLocales(context)
    return DeviceDebugInfo(
        appVersionName = packageInfo?.versionName.orEmpty(),
        appVersionCode = versionCode,
        flavor = config.provideFlavor(),
        buildType = config.provideBuildTypeName(),
        buildVariant = "${config.provideFlavor()}${config.provideBuildTypeName().replaceFirstChar { it.uppercase() }}",
        isDevFlavor = config.provideIsDevEnv(),
        packageName = context.packageName,
        installer = installerPackage(context),
        androidRelease = Build.VERSION.RELEASE.orEmpty(),
        sdkInt = Build.VERSION.SDK_INT,
        securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH
        } else null,
        incremental = Build.VERSION.INCREMENTAL,
        previewSdkInt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.PREVIEW_SDK_INT
        } else 0,
        manufacturer = Build.MANUFACTURER.orEmpty(),
        brand = Build.BRAND.orEmpty(),
        model = Build.MODEL.orEmpty(),
        device = Build.DEVICE.orEmpty(),
        product = Build.PRODUCT.orEmpty(),
        hardware = Build.HARDWARE.orEmpty(),
        board = Build.BOARD.orEmpty(),
        deviceName = deviceDisplayName(context),
        abis = Build.SUPPORTED_ABIS?.toList().orEmpty(),
        locale = Locale.getDefault().toLanguageTag(),
        locales = locales,
        timezone = TimeZone.getDefault().id,
        screenWidthPx = metrics.widthPixels,
        screenHeightPx = metrics.heightPixels,
        densityDpi = metrics.densityDpi,
        smallestWidthDp = context.resources.configuration.smallestScreenWidthDp,
        isEmulator = isEmulator(),
        totalRamMb = totalRamMb(context),
    )
}

private fun buildUserAgent(info: DeviceDebugInfo): String {
    return ("K-Office/${info.appVersionName} " +
        "(Android ${info.androidRelease}; SDK ${info.sdkInt}; " +
        "${info.manufacturer} ${info.model}; ${info.buildVariant})").headerSafe()
}

private fun deviceDisplayName(context: Context): String? {
    val fromSettings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
    } else null
    return fromSettings?.takeIf { it.isNotBlank() }
        ?: listOf(Build.MANUFACTURER, Build.MODEL).filter { it.isNotBlank() }.joinToString(" ")
}

private fun installerPackage(context: Context): String? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
    } else {
        @Suppress("DEPRECATION")
        context.packageManager.getInstallerPackageName(context.packageName)
    }
}.getOrNull()

private fun currentLocales(context: Context): List<String> {
    val config = context.resources.configuration
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val list = config.locales
        (0 until list.size()).map { list[it].toLanguageTag() }
    } else {
        @Suppress("DEPRECATION")
        listOfNotNull(config.locale?.toLanguageTag())
    }
}

private fun totalRamMb(context: Context): Long? = runCatching {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val mem = ActivityManager.MemoryInfo()
    manager.getMemoryInfo(mem)
    mem.totalMem / (1024 * 1024)
}.getOrNull()

private fun isEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT.orEmpty()
    val model = Build.MODEL.orEmpty()
    val product = Build.PRODUCT.orEmpty()
    val manufacturer = Build.MANUFACTURER.orEmpty()
    return fingerprint.startsWith("generic") ||
        fingerprint.contains("unknown") ||
        model.contains("Emulator", ignoreCase = true) ||
        model.contains("Android SDK", ignoreCase = true) ||
        manufacturer.contains("Genymotion", ignoreCase = true) ||
        product.contains("sdk", ignoreCase = true) ||
        product.contains("emulator", ignoreCase = true)
}

private fun String.headerSafe(): String =
    replace(Regex("[\\r\\n]"), " ")
        .map { ch -> if (ch.code in 32..126) ch else '?' }
        .joinToString("")
        .take(180)
