import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-detekt2")
  alias(libs.plugins.allopen)
  alias(libs.plugins.benchmarks)
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
