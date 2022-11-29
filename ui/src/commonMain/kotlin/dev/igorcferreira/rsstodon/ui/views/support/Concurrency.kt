package dev.igorcferreira.rsstodon.ui.views.support

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun async(block: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.IO)
    .launch(block = block)