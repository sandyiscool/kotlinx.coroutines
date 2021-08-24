/*
 * Copyright 2016-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.coroutines.internal

import kotlinx.coroutines.*
import kotlin.coroutines.*

internal class SlicedDispatcher(
    private val dispatcher: ExecutorCoroutineDispatcher,
    private val parallelism: Int
) : CoroutineDispatcher(), Runnable, Delay by (dispatcher as? Delay ?: DefaultDelay) {

    @InternalCoroutinesApi
    override fun dispatchYield(context: CoroutineContext, block: Runnable) {
        super.dispatchYield(context, block)
    }

    @Volatile
    private var runningWorkers = 0
    private val queue = LockFreeTaskQueue<Runnable>(singleConsumer = false)

    // TODO ???
    private var fairnessCounter = 0

    override fun run() {
        while (true) {
            val task = queue.removeFirstOrNull()
            // TODO emulate fairness and re-submit itself?
            if (task != null) {
                task.run()
                continue
            }

            synchronized(this) {
                --runningWorkers
                if (queue.size == 0) return
                ++runningWorkers
            }
        }
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        // Add task to queue so running workers will be able to see that
        queue.addLast(block)
        if (runningWorkers >= parallelism) {
            return
        }

        synchronized(this) {
            if (runningWorkers >= parallelism) return
            ++runningWorkers
        }
        dispatcher.executor.execute(this)
    }
}
