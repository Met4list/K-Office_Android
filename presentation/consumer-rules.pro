# Keep domain classes
-keep class com.k_office.domain.** { *; }

# Keep presentation worker classes
-keep class com.k_office.presentation.worker.** { *; }

# StringConcatFactory for Java 11+
-dontwarn java.lang.invoke.StringConcatFactory

-keepnames class **.args.** { *; }

# Keep SafeArgs generated classes
-keep class * implements androidx.navigation.NavArgs { *; }

# Keep custom argument types used in navigation
-keep class **.args.** { *; }

# Keep Parcelable classes
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