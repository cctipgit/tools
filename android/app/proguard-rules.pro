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

# 如果项目中使用到注解，需要保留注解属性
-keepattributes *Annotation*

# 屏蔽警告
-ignorewarnings

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

#表示不混淆枚举中的values()和valueOf()方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# Keep native methods
-keepclassmembers class * {
    native <methods>;
}
#Gson混淆配置
#避免混淆泛型, 这在JSON实体映射时非常重要
-dontwarn com.google.gson.**
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *;}
# Application classes that will beserialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *;}
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep interface com.google.gson.**{*;}
# 实体类不混淆
-keep class com.hash.coinconvert.entity.** {*;}
-keep class com.exchange2currency.ef.currencyprice.grpc.**{*;}
# butterknife不混淆
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
# okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
-keep interface okhttp3.**{*;}
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

# liveeventbus
-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }

# protobuf
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }

# appsflyer
-keep class com.appsflyer.** { *; }
# Google Play Install Referrer for appsflyer
-keep public class com.android.installreferrer.** { *; }