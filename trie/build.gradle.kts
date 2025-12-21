plugins {
  id("com.android.lint")
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-kmp-library")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

kotlin {
  defaultKmpTargets(
    project,
    androidNamespace = "com.eygraber.trie",
  )

  sourceSets {
    commonMain.dependencies {
      implementation(libs.androidx.collections)
    }

    commonTest.dependencies {
      implementation(projects.utils)

      implementation(libs.test.kotlin)
      implementation(libs.test.kotlin.annotations)
    }
  }
}
