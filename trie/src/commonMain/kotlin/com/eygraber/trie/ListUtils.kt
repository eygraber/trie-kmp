package com.eygraber.trie

internal inline fun <T> List<T>.fastForEach(block: (T) -> Unit) {
  if(this is RandomAccess) {
    for(i in 0 until size) {
      block(this[i])
    }
  }
  else {
    for(element in this) {
      block(element)
    }
  }
}

internal fun <T> List<T>.commonPrefixLength(other: List<T>): Int =
  if(this is RandomAccess && other is RandomAccess) {
    var commonPrefixLength = 0
    val minLength = minOf(size, other.size)
    for(i in 0 until minLength) {
      if(this[i] == other[i]) {
        commonPrefixLength++
      }
      else {
        break
      }
    }
    commonPrefixLength
  }
  else {
    zip(other).takeWhile { it.first == it.second }.count()
  }

internal fun <T> List<T>.fastDrop(n: Int): List<T> =
  if(this is RandomAccess) {
    require(n >= 0)

    if(n == 0) {
      this
    }
    else if(n >= size) {
      emptyList()
    }
    else {
      // n is at least 1 and size is at least 2
      subList(fromIndex = n, toIndex = size)
    }
  }
  else {
    drop(n)
  }
