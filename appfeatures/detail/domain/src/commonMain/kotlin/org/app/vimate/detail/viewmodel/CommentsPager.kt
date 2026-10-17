package org.app.vimate.detail.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.app.vimate.detail.repository.DetailRepository
import org.app.vimate.sharedlogic.yt.Comment

/** POC CommentsPanel paging: loads pages of comments until the continuation runs out. */
class CommentsPager(private val repo: DetailRepository, private val scope: CoroutineScope, token: String) {
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private var nextToken: String? = token

    fun more() {
        val t = nextToken ?: return
        if (_loading.value) return
        _loading.value = true
        scope.launch {
            runCatching { repo.comments(t) }.onSuccess { _comments.value += it.items; nextToken = it.continuation }.onFailure { nextToken = null }
            _loading.value = false
        }
    }
}
