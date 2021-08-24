/*
 * Copyright 2016-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.coroutines

public abstract class SliceableCoroutineDispatcher : CoroutineDispatcher() {

    public abstract fun slice(parallelism: Int): CoroutineDispatcher
}
