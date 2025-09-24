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

# Keep SafeArgs generated classes (these have the NavArgs implementations)
-keep class * implements androidx.navigation.NavArgs { *; }

# Keep custom argument types used in navigation
-keep class **.args.** { *; }

# Keep Parcelable classes used in navigation
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
}

# Keep reflection used by SafeArgs
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }

# CRITICAL: Keep class names for reflection in SafeArgs
-keepnames class * implements androidx.navigation.NavArgs
-keepnames class **.args.**

# JavaPoet - annotation processing library (compile-time only)
-dontwarn javax.lang.model.**
-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.Modifier
-dontwarn javax.lang.model.type.TypeMirror
-dontwarn javax.lang.model.type.TypeVisitor
-dontwarn javax.lang.model.util.SimpleTypeVisitor8
-dontwarn javax.annotation.processing.**
-dontwarn javax.tools.**

# Keep JavaPoet if it's being included in the final APK
-keep class com.squareup.javapoet.** { *; }
