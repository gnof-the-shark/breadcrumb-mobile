package com.paul.infrastructure.web

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

actual fun createHttpClientEngine(): HttpClientEngineFactory<*> = Darwin

actual fun platformInfo(): String {
    val device = UIDevice.currentDevice
    return "(${device.systemName} ${device.systemVersion} ${device.model})"
}

actual fun versionName(): String {
    val dict = NSBundle.mainBundle.infoDictionary ?: return "unknown"
    return dict["CFBundleShortVersionString"] as? String ?: "unknown"
}
