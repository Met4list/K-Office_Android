# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep attributes for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepattributes *Annotation*

-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.Modifier
-dontwarn javax.lang.model.type.TypeMirror
-dontwarn javax.lang.model.type.TypeVisitor
-dontwarn javax.lang.model.util.SimpleTypeVisitor8

# Keep all custom Args classes and their members
-keep class com.k_office.presentation.base.utils.Args { *; }
-keep class * implements com.k_office.presentation.base.utils.Args { *; }
-keepclassmembers class * implements com.k_office.presentation.base.utils.Args { *; }

# Keep VerifyOtpArgs specifically
-keep class com.k_office.presentation.screen.verify_otp.args.** { *; }
-keepclassmembers class com.k_office.presentation.screen.verify_otp.args.** {
    <fields>;
    <init>(...);
}

# Keep all Parcelable creators
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep Parcelize generated code
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Preserve the names of fields in Parcelable classes (critical for serialization)
-keepclassmembers class * implements android.os.Parcelable {
    <fields>;
}