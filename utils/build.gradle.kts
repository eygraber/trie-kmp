import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.android.lint")
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-kmp-library")
  id("com.eygraber.conventions-detekt2")
}

kotlin {
  defaultKmpTargets(
    project,
    androidNamespace = "com.eygraber.trie.utils",
  )

  sourceSets {
    commonMain.dependencies {
      implementation(projects.trie)
    }
  }
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
