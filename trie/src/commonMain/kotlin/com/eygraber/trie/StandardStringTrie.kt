package com.eygraber.trie

/**
 * A high-performance, non-compact [MutableTrie] implementation specialized for String keys.
 *
 * This implementation is generally faster for individual key lookups (`get`, `containsKey`)
 * than a [CompactStringViewTrie] due to its simpler logic, but it uses more memory.
 *
 * @param V The type of the values.
 */
public class StandardStringTrie<V> : Trie<String, V>, AbstractTrie<String, V>() {
  private val root = StandardTrieNode<Char, V>()

  override var size: Int = 0
    private set

  override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
    get() = object : AbstractMutableSet<MutableMap.MutableEntry<String, V>>() {
      override val size: Int get() = this@StandardStringTrie.size

      override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, V>> {
        val allEntries = mutableListOf<MutableMap.MutableEntry<String, V>>()
        val path = StringBuilder()

        fun collect(node: StandardTrieNode<Char, V>) {
          if(node.isKeyNode()) {
            allEntries.add(TrieEntry(path.toString(), requireNotNull(node.value)))
          }
          node.children.forEach { char, childNode ->
            path.append(char)
            collect(childNode)
            path.setLength(path.length - 1) // Backtrack
          }
        }

        collect(root)

        val backingIterator = allEntries.iterator()
        var lastEntry: MutableMap.MutableEntry<String, V>? = null

        return object : MutableIterator<MutableMap.MutableEntry<String, V>> {
          override fun hasNext() = backingIterator.hasNext()

          override fun next(): MutableMap.MutableEntry<String, V> {
            lastEntry = backingIterator.next()
            return lastEntry
          }

          override fun remove() {
            checkNotNull(lastEntry)
            this@StandardStringTrie.remove(lastEntry.key)
            backingIterator.remove()
          }
        }
      }

      override fun add(element: MutableMap.MutableEntry<String, V>) = throw UnsupportedOperationException()
    }

  override operator fun get(key: String): V? {
    var currentNode = root

    for(i in 0 until key.length) {
      currentNode = currentNode.children[key[i]] ?: return null
    }

    return currentNode.value
  }

  override fun put(key: String, value: V): V? {
    var currentNode = root

    for(i in 0 until key.length) {
      val currentValue = currentNode.children[key[i]]
      currentNode = currentValue ?: StandardTrieNode<Char, V>().also { currentNode.children[key[i]] = it }
    }

    val oldValue = currentNode.value
    currentNode.value = value
    if(oldValue == null) {
      size++
    }

    return oldValue
  }

  @Suppress("ReturnCount")
  override fun remove(key: String): V? {
    val path = mutableListOf<Pair<StandardTrieNode<Char, V>, Char>>()
    var currentNode = root

    for(char in key) {
      val child = currentNode.children[char] ?: return null
      path.add(currentNode to char)
      currentNode = child
    }

    val oldValue = currentNode.value ?: return null
    currentNode.value = null
    size--

    // Prune dangling nodes
    for(i in path.size - 1 downTo 0) {
      val (parent, char) = path[i]
      val childNode = requireNotNull(parent.children[char])

      if(childNode.children.isEmpty() && !childNode.isKeyNode()) {
        parent.children.remove(char)
      }
      else {
        // Stop as soon as we find a node that shouldn't be removed
        break
      }
    }

    return oldValue
  }

  override fun clear() {
    root.children.clear()
    size = 0
  }

  override fun startsWith(prefix: String): Boolean {
    var currentNode = root

    for(i in 0 until prefix.length) {
      currentNode = currentNode.children[prefix[i]] ?: return false
    }

    return true
  }

  override fun getAllWithPrefix(prefix: String): Map<String, V> {
    var startNode = root

    for(i in 0 until prefix.length) {
      startNode = startNode.children[prefix[i]] ?: return emptyMap()
    }

    val results = mutableMapOf<String, V>()
    val path = StringBuilder(prefix)

    if(startNode.isKeyNode()) {
      results[prefix] = requireNotNull(startNode.value)
    }

    collectAll(startNode, path, results)
    return results
  }

  override fun getAllValuesWithPrefix(prefix: String): Collection<V> {
    var startNode = root

    for(i in 0 until prefix.length) {
      startNode = startNode.children[prefix[i]] ?: return emptyList()
    }

    val results = mutableListOf<V>()
    if(startNode.isKeyNode()) {
      results.add(requireNotNull(startNode.value))
    }

    collectAllValues(startNode, results)

    return results
  }

  private fun collectAll(node: StandardTrieNode<Char, V>, path: StringBuilder, results: MutableMap<String, V>) {
    node.children.forEach { char, childNode ->
      path.append(char)
      if(childNode.isKeyNode()) {
        results[path.toString()] = requireNotNull(childNode.value)
      }
      collectAll(childNode, path, results)
      path.setLength(path.length - 1)
    }
  }

  private fun collectAllValues(node: StandardTrieNode<Char, V>, results: MutableList<V>) {
    node.children.forEachValue { childNode ->
      if(childNode.isKeyNode()) {
        results.add(requireNotNull(childNode.value))
      }
      collectAllValues(childNode, results)
    }
  }
}
