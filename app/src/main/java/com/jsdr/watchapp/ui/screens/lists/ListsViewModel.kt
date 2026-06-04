package com.jsdr.watchapp.ui.screens.lists

import androidx.lifecycle.ViewModel
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.data.repository.CURRENT_USER
import com.jsdr.watchapp.domain.models.MediaOverview
import com.jsdr.watchapp.ui.screens.home.HomeViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ListsViewState(
    val currentUser: String = CURRENT_USER,
    val language: String = "pl-PL",
    val mediaList: List<MediaOverview> = emptyList(),
    val selectedMediaId: Int? = null,
    val usersLists: List<UserList> = emptyList(),
    val error: String? = null
)

//data class ListContentsViewState()

class ListsViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(ListsViewState())
    val viewState: StateFlow<ListsViewState> = _viewState.asStateFlow()
}