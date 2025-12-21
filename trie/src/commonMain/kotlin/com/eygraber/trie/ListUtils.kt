package com.eygraber.trie

@PublishedApi
internal inline fun <T> Collection<T>.fastForEach(block: (T) -> Unit) {
  val list = this as? List<T> ?: toList()
  if(list is RandomAccess) {
    for(i in 0 until size) {
      block(list[i])
    }
  }
  else {
    for(element in this) {
      block(element)
    }
  }
}

@Suppress("ReturnCount")
internal fun <T> List<T>.commonPrefixLength(other: List<T>, otherOffset: Int = 0): Int {
  if(this is RandomAccess && other is RandomAccess) {
    val minLength = minOf(this.size, other.size - otherOffset)
    for(i in 0 until minLength) {
      if(this[i] != other[i + otherOffset]) {
        return i
      }
    }
    return minLength
  }
  else {
    return asSequence().zip(other.asSequence()).takeWhile { it.first == it.second }.count()
  }
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
