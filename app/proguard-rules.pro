# Coil
-keep class coil.** { *; }
-dontwarn coil.**

# Firebase
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Firestore
-keep class com.example.alphakids.data.firebase.models.** { *; }
