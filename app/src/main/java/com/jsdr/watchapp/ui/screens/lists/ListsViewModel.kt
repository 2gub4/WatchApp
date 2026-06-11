package com.jsdr.watchapp.ui.screens.lists

import androidx.compose.ui.graphics.Color
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
                val defaultOrder = listOf("favourites", "bucketlist", "watched", "rated")
                val defaultLists = lists
                    .filter {
                        it.id in defaultOrder
                    }
                    .sortedBy {
                        defaultOrder.indexOf(it.id)
                    }
                val customLists = lists.filter { it.id !in defaultOrder }
                    .sortedBy { it.creationDate?.time ?: System.currentTimeMillis() }
                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                    usersLists = defaultLists + customLists
                )
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun createList(name: String, description: String) {
        viewModelScope.launch {
            try {
                val newList = UserList(name = name, description = description)
                val currentLists = _viewState.value.usersLists.toMutableList()
                currentLists.add(newList)
                _viewState.value = _viewState.value.copy(usersLists = currentLists)
                WatchAppRepository.Lists.createList(newList)
                loadLists()
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(error = e.message)
            }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            try {
                WatchAppRepository.Lists.deleteList(listId)
                loadLists()
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(error = e.message)
            }
        }
    }

    fun renameList(listId: String, newName: String) {
        viewModelScope.launch {
            try {
                WatchAppRepository.Lists.changeListName(listId, newName)
                loadLists()
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(error = e.message)
            }
        }
    }

    fun changeListColor(listId: String, newColor: Color) {
        viewModelScope.launch {
            val user = WatchAppRepository.currentUserFlow.value
            if (user != null) {
                WatchAppRepository.Lists.changeListColor(listId, newColor)
                loadLists()
            }
        }
    }

}