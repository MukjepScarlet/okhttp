/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.recipes.kt

import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request

class CancelCall {
  private val executor = Executors.newScheduledThreadPool(1)
  private val client = OkHttpClient()

  fun run() {
    val request =
      Request
        .Builder()
        .url("http://httpbin.org/delay/2") // This URL is served with a 2 second delay.
        .build()

    val startNanos = System.nanoTime()
    val call = client.newCall(request)

    // Schedule a job to cancel the call in 1 second.
    executor.schedule({
      System.out.printf("%.2f Canceling call.%n", (System.nanoTime() - startNanos) / 1e9f)
      call.cancel()
      System.out.printf("%.2f Canceled call.%n", (System.nanoTime() - startNanos) / 1e9f)
    }, 1, TimeUnit.SECONDS)

    System.out.printf("%.2f Executing call.%n", (System.nanoTime() - startNanos) / 1e9f)
    try {
      call.execute().use { response ->
        System.out.printf(
          "%.2f Call was expected to fail, but completed: %s%n",
          (System.nanoTime() - startNanos) / 1e9f,
          response,
        )
      }
    } catch (e: IOException) {
      System.out.printf(
        "%.2f Call failed as expected: %s%n",
        (System.nanoTime() - startNanos) / 1e9f,
        e,
      )
    }
  }
}

fun main() {
  CancelCall().run()
}
