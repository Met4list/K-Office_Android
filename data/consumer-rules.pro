# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not kept
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Gson (если используете)
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep your API models
-keep class com.k_office.data.network.model.** { *; }
-keep class com.k_office.domain.model.** { *; }

# Protobuf - уже есть
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
    *** getDefaultInstance();
}

# Keep all protobuf classes
-keep class com.google.protobuf.** { *; }

# Keep DataStore proto classes
-keep class com.k_office.data.storage.** { *; }

# DataStore
-keep class androidx.datastore.*.** { *; }
-keepclassmembers class androidx.datastore.preferences.protobuf.** { *; }

# Don't obfuscate proto field names
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    private <fields>;
}

# New rules

# Keep all data layer models (requests and responses)
-keep class com.k_office.data.request.** { *; }
-keep class com.k_office.data.response.** { *; }
-keep class com.k_office.data.network.model.** { *; }

# Keep all fields in these classes
-keepclassmembers class com.k_office.data.request.** {
    <fields>;
}
-keepclassmembers class com.k_office.data.response.** {
    <fields>;
}

# Kotlinx Serialization (if you're using it)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.k_office.data.**$$serializer { *; }
-keepclassmembers class com.k_office.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.k_office.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}