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
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# Twilio
-keep class com.twilio.conversations.** { *; }

##------------------------Disable Log ------------------------
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
    public static *** i(...);
    public static *** e(...);
}

#lottie
-keep class com.airbnb.lottie.samples.** { *; }


-keep class com.google.android.** { *; }
-keep class com.google.firebase.** { *; }
-keep class de.fast2work.mobility.data.model.** { *; }
-keep class de.fast2work.mobility.data.remote.** { *; }
-keep class de.fast2work.mobility.data.request.** { *; }
-keep class de.fast2work.mobility.data.response.** { *; }
-keep class de.fast2work.mobility.utility.** { *; }

-keep class de.fast2work.mobility.data.remote.** { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##AndroidX
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

#====== OkHttp
-dontnote okhttp3.**
-dontnote okio.**
-keepattributes *Annotation*
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

#=====square http start
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.* { *;}
-keep class com.squareup.okhttp3.** {*;}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
#=====square http end

-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

#=== Exoplayer2 start start
-dontwarn com.google.android.exoplayer2.**

#=== Exoplayer2 start end


#====Retrofit start
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions.**

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
#====Retrofit end


##======   Google Play start
-keep class com.google.android.gms.tasks** {*;}

-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}
# Keep SafeParcelable value, needed for reflection. This is required to support backwards
# compatibility of some classes.
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
# Keep the names of classes/members we need for client functionality.
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
# Needed for Parcelable/SafeParcelable Creators to not get stripped
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keep class com.google.android.gms.measurement.** { *; }
-dontwarn com.google.android.gms.measurement.**
-keep public class com.google.android.gms.common.** {
    public protected *;
}
-keep public class com.google.android.gms.gcm.** {
    public protected *;
}
##====== Google Play Services end

##=====androidx.lifecycle start
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}
##=====androidx.lifecycle end

#====Glide App start
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
#====Glide App end

#====Event Bus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#====Event end

-dontwarn kotlin.**
-dontwarn org.jetbrains.annotations.**

##ffmpeg start
#-keep class com.arthenica.mobileffmpeg.Config {
#    native <methods>;
#    void log(long, int, byte[]);
#    void statistics(long, int, float, float, long , int, double, double);
#}
#
#-keep class com.arthenica.mobileffmpeg.AbiDetect {
#    native <methods>;
#}
##ffmpeg end

#googlecalender start
-keep class com.google.api.services.calendar.** {
    *;
}

-keepclassmembers class com.google.api.services.calendar.** {
    *;
}

# Exclude org.apache.httpcomponents and all of its sub-packages
-dontwarn org.apache.httpcomponents.**
#googlecalender end

#googleclient start
# Keep all classes and their members in the com.google.android.gms package and its subpackages
-keep class com.google.android.gms.** {
    *;
}

# Keep all classes and their members in the com.google.api.client package and its subpackages
-keep class com.google.api.client.** {
    *;
}

# Keep all classes and their members in the com.google.api.services package and its subpackages
-keep class com.google.api.services.** {
    *;
}

# Keep all classes and their members in the com.google.gson package and its subpackages
-keep class com.google.gson.** {
    *;
}
#googleclient end


# Keep all classes and methods in Guava
-keep class com.google.common.** {
    *;
}

-keepclassmembers class * {
    java.lang.Class getClass();
}

-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation


-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }


# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
-keepclasseswithmembers class * {
    <init>(...);
    @com.google.gson.annotations.SerializedName <fields>;
}
# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken


#added for the missing_rules.txt file
-dontwarn kotlinx.serialization.KSerializer
-dontwarn kotlinx.serialization.Serializable
-dontwarn kotlinx.serialization.internal.AbstractPolymorphicSerializer

-keep class com.vesputi.mobilitybox_ticketing_android.** { *; }