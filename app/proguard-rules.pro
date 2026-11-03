# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\stnieder\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# SPDX-FileCopyrightText: 2015-2024 Nextcloud GmbH and Nextcloud contributors
# SPDX-FileCopyrightText: 2015-2024 Stefan Niedermann <info@niedermann.it>
# SPDX-License-Identifier: GPL-3.0-or-later

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*, SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# Single sign-on requests, responses and exceptions cross the process boundary to the Files app
# and are deserialized there by class name.
-keep class com.nextcloud.android.sso.** { *; }
-keep interface com.nextcloud.android.sso.** { *; }

# Retrofit interfaces are proxied by the SSO library, which reads generic return types to pick the Gson type.
-keep interface it.niedermann.owncloud.notes.persistence.sync.** { *; }

-keep class it.niedermann.owncloud.notes.shared.model.** { <fields>; <init>(...); }
-keep class it.niedermann.owncloud.notes.persistence.entity.** { <fields>; <init>(...); }
-keep class it.niedermann.owncloud.notes.persistence.sync.NotesAPI$* { <fields>; <init>(...); }
-keep class it.niedermann.owncloud.notes.share.model.** { <fields>; <init>(...); }
-keep class com.owncloud.android.lib.** { <fields>; <init>(...); }
-keep class com.nextcloud.android.lib.** { <fields>; <init>(...); }

# commons-httpclient 3.x resolves its own classes by name in static initializers (javac class$ pattern).
-keep class org.apache.commons.httpclient.** { *; }
# WorkManager stores worker class names; jobs queued before an upgrade must still resolve.
-keep class * extends androidx.work.ListenableWorker { <init>(...); }

# Markwon's optional GIF plugin is compiled against android-gif-drawable, which this app does not ship.
-dontwarn pl.droidsonroids.gif.**
