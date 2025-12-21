import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt2")
}

android {
  namespace = "com.eygraber.trie.utils"
}

kotlin {
  defaultKmpTargets(project)

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
