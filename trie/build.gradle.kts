plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

android {
  namespace = "com.eygraber.trie"
}

kotlin {
  defaultKmpTargets(project)

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
