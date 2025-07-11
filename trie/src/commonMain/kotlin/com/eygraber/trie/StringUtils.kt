package com.eygraber.trie

internal fun CharSequence.commonPrefixLength(other: CharSequence): Int {
  val minLength = minOf(length, other.length)
  for(i in 0 until minLength) {
    if(this[i] != other[i]) {
      return i
    }
  }
  return minLength
}
