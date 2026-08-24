import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt2")
  alias(libs.plugins.allopen)
  // Applied from the root buildscript classpath (not via alias) so that the newer
  // kotlin-util-klib(-metadata) forced there wins over the stale 2.2.0 pins in
  // kotlinx-benchmark-plugin 0.4.18. See the comment in the root build.gradle.kts.
  id("org.jetbrains.kotlinx.benchmark")
}

// Belt and braces for the same kotlinx-benchmark 0.4.18 issue: if the benchmark source
// generator resolves its worker classpath through a project configuration, make sure the
// klib utils match the project's Kotlin version instead of the plugin's stale 2.2.0 pins.
configurations.configureEach {
  resolutionStrategy {
    force(
      "org.jetbrains.kotlin:kotlin-util-klib:${libs.versions.kotlin.get()}",
      "org.jetbrains.kotlin:kotlin-util-klib-metadata:${libs.versions.kotlin.get()}",
    )
  }
}

kotlin {
  kmpTargets(
    KmpTarget.Ios,
    KmpTarget.Js,
    KmpTarget.Jvm,
    KmpTarget.Linux,
    KmpTarget.Macos,
    KmpTarget.Mingw,
    project = project,
    webOptions = KmpTarget.WebOptions(
      isNodeEnabled = true,
      isBrowserEnabled = false,
    ),
  )

  sourceSets {
    commonMain.dependencies {
      implementation(projects.trie)
      implementation(projects.utils)

      implementation(libs.kotlinx.benchmarks)
    }
  }
}

allOpen {
  annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
  targets {
    register("js")
    register("jvm")
    register("linuxX64")
    register("mingwX64")
    register("macosArm64")
    register("macosX64")
  }
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
