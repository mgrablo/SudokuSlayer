# Keep Protobuf GeneratedMessageLite fields.
# R8 / ProGuard will otherwise obfuscate or remove fields accessed by Datastore reflection.
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}
