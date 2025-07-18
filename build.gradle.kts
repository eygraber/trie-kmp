import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
  dependencies {
    classpath(libs.buildscript.android)
    classpath(libs.buildscript.androidCacheFix)
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.dokka)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.publish)
  }
}

plugins {
  base
  alias(libs.plugins.conventions)
}

deleteRootBuildDirWhenCleaning()

gradleConventionsDefaults {
  android {
    sdkVersions(
      compileSdk = libs.versions.android.sdk.compile,
      targetSdk = libs.versions.android.sdk.target,
      minSdk = libs.versions.android.sdk.min,
    )
  }

  detekt {
    plugins(
      libs.detektEygraber.formatting,
      libs.detektEygraber.style,
    )
  }

  kotlin {
    jvmTargetVersion = JvmTarget.JVM_11
    explicitApiMode = ExplicitApiMode.Strict
  }
}

gradleConventionsKmpDefaults {
  webOptions = webOptions.copy(
    isNodeEnabled = true,
    isBrowserEnabled = true,
    isBrowserEnabledForLibraryTests = false,
  )

  targets(
    KmpTarget.Android,
    // KmpTarget.AndroidNative, https://issuetracker.google.com/issues/430991573
    KmpTarget.Ios,
    KmpTarget.Js,
    KmpTarget.Jvm,
    KmpTarget.Linux,
    KmpTarget.Macos,
    KmpTarget.Mingw,
    KmpTarget.Tvos,
    KmpTarget.WasmJs,
    // KmpTarget.WasmWasi, https://issuetracker.google.com/issues/430991573
    KmpTarget.Watchos,
  )
}
