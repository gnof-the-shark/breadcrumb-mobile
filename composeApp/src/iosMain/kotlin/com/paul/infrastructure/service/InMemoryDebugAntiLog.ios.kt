package com.paul.infrastructure.service

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun platformCurrentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
