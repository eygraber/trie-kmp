package com.eygraber.trie

internal fun String.commonPrefixLength(other: String, otherOffset: Int = 0): Int {
  val minLength = minOf(this.length, other.length - otherOffset)
  for(i in 0 until minLength) {
    if(this[i] != other[i + otherOffset]) {
      return i
    }
  }
  return minLength
}
