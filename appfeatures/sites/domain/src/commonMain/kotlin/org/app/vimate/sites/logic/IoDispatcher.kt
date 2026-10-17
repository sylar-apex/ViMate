package org.app.vimate.sites.logic

import kotlinx.coroutines.CoroutineDispatcher

/** Dispatcher for blocking probe work (Dispatchers.IO where it exists). */
expect val ioDispatcher: CoroutineDispatcher
