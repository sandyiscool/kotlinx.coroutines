/*
 * Copyright 2016-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.coroutines.internal

internal actual fun <E> subscriberList(): SubscribersList<E> = CopyOnWriteList()

internal actual fun <E> identitySet(expectedSize: Int): MutableSet<E> = HashSet(expectedSize)
