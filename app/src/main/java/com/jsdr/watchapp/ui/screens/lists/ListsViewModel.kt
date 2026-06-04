package com.jsdr.watchapp.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.data.repository.WatchAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ListsViewState(
    val isLoading: Boolean = false,
    val usersLists: List<UserList> = emptyList(),
    val error: String? = null
)

class ListsViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(ListsViewState())
    val viewState: StateFlow<ListsViewState> = _viewState.asStateFlow()

    init {
        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true, error = null)
            try {
                val lists = WatchAppRepository.Lists.getUserLists()
                _viewState.value = _viewState.value.copy(isLoading = false, usersLists = lists)
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun createList(name: String, description: String) {
        viewModelScope.launch {
            try {
                val newList = UserList(name = name, description = description)
                WatchAppRepository.Lists.createList(newList)
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(error = e.message)
            }
        }
    }
}