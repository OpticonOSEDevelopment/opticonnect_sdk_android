# Keep all classes in the relocated Dagger package
-keep class com.opticon.opticonnect.dagger.** { *; }

# Keep all classes in the relocated javax.inject package
-keep class com.opticon.opticonnect.javax.inject.** { *; }

# Keep all classes in the relocated Jakarta package
-keep class com.opticon.opticonnect.jakarta.** { *; }

# Keep all classes in the relocated com.google.dagger package
-keep class com.opticon.opticonconnect.com.google.dagger.** { *; }

# Keep all interfaces in the relocated Dagger package (if necessary)
-keep interface com.opticon.opticonconnect.dagger.** { *; }

# Keep all classes in the relocated org.jetbrains.annotations package
-keep class com.opticon.opticonconnect.org.jetbrains.annotations.** { *; }

# Keep all classes in the relocated Timber package
-keep class com.opticon.opticonconnect.com.jakewharton.timber.** { *; }

# Keep all classes in the relocated RxJava package
-keep class com.opticon.opticonconnect.io.reactivex.** { *; }

# Keep your SDK's internal DI classes
-keep class com.opticon.opticonconnect.sdk.internal.di.** { *; }

# Keep your SDK's public API
-keep class com.opticon.opticonconnect.** { *; }

# Keep Dagger-generated components
-keep class com.opticon.opticonconnect.dagger.Dagger* { *; }

# Keep annotation classes
-keep @interface com.opticon.opticonconnect.org.jetbrains.annotations.** { *; }

# Keep public API classes and their members
-keep public class com.opticon.opticonconnect.** {
    public *;
}
