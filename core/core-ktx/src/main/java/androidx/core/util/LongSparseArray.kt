/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE") // Aliases to public API.

package androidx.core.util

import android.annotation.SuppressLint
import android.util.LongSparseArray
import androidx.annotation.RequiresApi

/** Returns the number of key/value pairs in the collection. */
@get:RequiresApi(16)
@get:SuppressLint("ClassVerificationFailure")
public inline val <T> LongSparseArray<T>.size: Int get() = size()

/** Returns true if the collection contains [key]. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public inline operator fun <T> LongSparseArray<T>.contains(key: Long): Boolean =
    indexOfKey(key) >= 0

/** Allows the use of the index operator for storing values in the collection. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public inline operator fun <T> LongSparseArray<T>.set(key: Long, value: T): Unit = put(key, value)

/** Creates a new collection by adding or replacing entries from [other]. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public operator fun <T> LongSparseArray<T>.plus(other: LongSparseArray<T>): LongSparseArray<T> {
    val new = LongSparseArray<T>(size() + other.size())
    new.putAll(this)
    new.putAll(other)
    return new
}

/** Returns true if the collection contains [key]. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public inline fun <T> LongSparseArray<T>.containsKey(key: Long): Boolean = indexOfKey(key) >= 0

/** Returns true if the collection contains [value]. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public inline fun <T> LongSparseArray<T>.containsValue(value: T): Boolean =
    indexOfValue(value) >= 0

/** Return the value corresponding to [key], or [defaultValue] when not present. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public inline fun <T> LongSparseArray<T>.getOrDefault(key: Long, defaultValue: T): T =
    get(key) ?: defaultValue

/** Return the value corresponding to [key], or from [defaultValue] when not present. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public inline fun <T> LongSparseArray<T>.getOrElse(key: Long, defaultValue: () -> T): T =
    get(key) ?: defaultValue()

/** Return true when the collection contains no elements. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public inline fun <T> LongSparseArray<T>.isEmpty(): Boolean = size() == 0

/** Return true when the collection contains elements. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public inline fun <T> LongSparseArray<T>.isNotEmpty(): Boolean = size() != 0

/** Removes the entry for [key] only if it is mapped to [value]. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public fun <T> LongSparseArray<T>.remove(key: Long, value: T): Boolean {
    val index = indexOfKey(key)
    if (index >= 0 && value == valueAt(index)) {
        removeAt(index)
        return true
    }
    return false
}

/** Update this collection by adding or replacing entries from [other]. */
@RequiresApi(16)
public fun <T> LongSparseArray<T>.putAll(other: LongSparseArray<T>): Unit = other.forEach(::put)

/** Performs the given [action] for each key/value entry. */
@RequiresApi(16)
@SuppressLint("ClassVerificationFailure")
public inline fun <T> LongSparseArray<T>.forEach(action: (key: Long, value: T) -> Unit) {
    for (index in 0 until size()) {
        action(keyAt(index), valueAt(index))
    }
}

/** Return an iterator over the collection's keys. */
@RequiresApi(16)
public fun <T> LongSparseArray<T>.keyIterator(): LongIterator = object : LongIterator() {
    var index = 0

    @SuppressLint("ClassVerificationFailure")
    override fun hasNext() = index < size()

    @SuppressLint("ClassVerificationFailure")
    override fun nextLong() = keyAt(index++)
}

/** Return an iterator over the collection's values. */
@RequiresApi(16)
public fun <T> LongSparseArray<T>.valueIterator(): Iterator<T> = object : Iterator<T> {
    var index = 0

    @SuppressLint("ClassVerificationFailure")
    override fun hasNext() = index < size()

    @SuppressLint("ClassVerificationFailure")
    override fun next() = valueAt(index++)
}
