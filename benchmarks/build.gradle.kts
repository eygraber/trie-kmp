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
  // kotlinx-benchmark 0.4.18 defaults the benchmark generator's kotlin-compiler-embeddable to
  // the project's Kotlin version, but its native source generator calls
  // KotlinLibrary.getModuleHeaderData(), which was removed from KotlinLibrary in Kotlin 2.3.20
  // (moved to KlibMetadataComponent), so generation fails with a NoSuchMethodError on any
  // project using Kotlin >= 2.3.20. Pin the generator's compiler to the last version that
  // still has that API. Remove once kotlinx-benchmark ships a fix.
  kotlinCompilerVersion = "2.3.10"

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
