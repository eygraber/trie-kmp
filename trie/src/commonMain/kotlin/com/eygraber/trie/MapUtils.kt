package com.eygraber.trie

import androidx.collection.SimpleArrayMap
import kotlin.collections.first

internal inline fun <K, V> SimpleArrayMap<K, V>.forEach(block: (K, V) -> Unit) {
  for(i in 0 until size()) {
    val key = keyAt(i)
    val value = valueAt(i)
    block(key, value)
  }
}

internal inline fun <V> SimpleArrayMap<*, V>.forEachValue(block: (V) -> Unit) {
  for(i in 0 until size()) {
    val value = valueAt(i)
    block(value)
  }
}

internal inline fun <V> Map<*, V>.forEachValue(block: (V) -> Unit) {
  forEach { (_, value) -> block(value) }
}

@Suppress("NOTHING_TO_INLINE")
internal inline operator fun <K, V> SimpleArrayMap<K, V>.set(key: K, value: V) = put(key, value)

@Suppress("NOTHING_TO_INLINE")
internal inline fun <V> SimpleArrayMap<*, V>.first() = valueAt(0)

@Suppress("NOTHING_TO_INLINE")
internal inline fun <V> Map<*, V>.first() = values.first()

@Suppress("NOTHING_TO_INLINE")
internal inline fun Map<*, *>.size() = size
