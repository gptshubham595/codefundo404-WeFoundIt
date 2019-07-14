# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Keval\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Keep name of all the classes
#-keepnames class ** { *; }
#
#-dontskipnonpubliclibraryclasses
#-forceprocessing
#-optimizationpasses 5
#-verbose
#
##support -v7 design lib
#-dontwarn android.support.design.**
#-keep class android.support.design.** { *; }
#-keep interface android.support.design.** { *; }
#-keep public class android.support.design.R$* { *; }
#
#-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}
#
##Keep anotations there
#-keepattributes *Annotation*
#
##Glide proguard config.
##see: 'https://github.com/bumptech/glide'
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#
##remove log class
#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#    public static *** v(...);
#    public static *** i(...);
#    public static *** w(...);
#    public static *** e(...);
#}