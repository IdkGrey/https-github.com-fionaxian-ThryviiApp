package com.thryvii.app.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thryvii.app.ThryviiApplication
import com.thryvii.app.data.Catalog
import com.thryvii.app.data.model.CatalogItem
import com.thryvii.app.data.model.JournalEntry
import com.thryvii.app.data.model.PurchaseResult
import com.thryvii.app.data.model.Quest
import com.thryvii.app.data.repository.GardenRepository
import com.thryvii.app.data.repository.JournalRepository
import com.thryvii.app.data.repository.QuestRepository
import com.thryvii.app.data.repository.SessionRepository
import com.thryvii.app.data.repository.SessionState
import com.thryvii.app.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Sign-in / onboarding. Shared by the whole nav graph (created once in ThryviiNavHost). */
class SessionViewModel(private val repo: SessionRepository) : ViewModel() {

    val session: StateFlow<SessionState> =
        repo.session.stateIn(viewModelScope, SharingStarted.Eagerly, SessionState(false, null))

    // The email typed on the first sign-up screen, held until SIGN UP on the password screen.
    private var pendingEmail = ""

    fun onEmailEntered(email: String) {
        pendingEmail = email
    }

    fun signUp() {
        viewModelScope.launch { repo.signUp(pendingEmail) }
    }

    fun signIn(usernameOrEmail: String) {
        viewModelScope.launch { repo.signIn(usernameOrEmail) }
    }

    fun saveGarden(gardenName: String, username: String, trackLocation: Boolean) {
        viewModelScope.launch { repo.saveGarden(gardenName, username, trackLocation) }
    }

    fun saveStarterSeed(seed: String) {
        viewModelScope.launch { repo.saveStarterSeed(seed) }
    }

    fun saveAvatar(hairstyleIndex: Int, colorIndex: Int) {
        viewModelScope.launch { repo.saveAvatar(hairstyleIndex, colorIndex) }
    }

    fun signOut() {
        viewModelScope.launch { repo.signOut() }
    }
}

/** An inventory entry joined with its catalog details, for the Add Plant / Add Decoration lists. */
data class OwnedItem(val item: CatalogItem, val owned: Int)

class GardenViewModel(private val garden: GardenRepository) : ViewModel() {

    val ownedPlants: StateFlow<List<OwnedItem>> = ownedOf(Catalog.plants)
    val ownedDecorations: StateFlow<List<OwnedItem>> = ownedOf(Catalog.decorations)

    private fun ownedOf(catalog: List<CatalogItem>): StateFlow<List<OwnedItem>> =
        garden.inventory
            .map { inventory ->
                catalog.mapNotNull { item ->
                    val count = inventory[item.key] ?: 0
                    if (count > 0) OwnedItem(item, count) else null
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun place(itemKey: String) {
        viewModelScope.launch { garden.place(itemKey) }
    }
}

class ShopViewModel(
    private val garden: GardenRepository,
    wallet: WalletRepository
) : ViewModel() {

    val coins: StateFlow<Int> =
        wallet.coins.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val mutableMessage = MutableStateFlow<String?>(null)

    /** Result of the latest purchase attempt, shown once and then cleared by [dismissMessage]. */
    val message: StateFlow<String?> = mutableMessage.asStateFlow()

    fun buy(catalogKey: String) {
        viewModelScope.launch {
            mutableMessage.value = when (val result = garden.purchase(catalogKey)) {
                is PurchaseResult.Success ->
                    "You got ${result.received.label}! (-${result.price} coins)"
                PurchaseResult.NotEnoughCoins -> "Not enough coins yet"
                PurchaseResult.UnknownItem -> "That item isn't available"
            }
        }
    }

    fun dismissMessage() {
        mutableMessage.value = null
    }
}

class JournalViewModel(private val repo: JournalRepository) : ViewModel() {

    val entries: StateFlow<List<JournalEntry>> =
        repo.entries.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun save(entry: JournalEntry) {
        viewModelScope.launch { repo.add(entry) }
    }
}

class QuestsViewModel(private val repo: QuestRepository) : ViewModel() {

    val quests: StateFlow<List<Quest>> =
        repo.quests.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun complete(questId: String) {
        viewModelScope.launch { repo.complete(questId) }
    }
}

/** Builds the ViewModels above, handing each the repositories it needs from the AppContainer. */
class ThryviiViewModelFactory(private val app: ThryviiApplication) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val c = app.container
        val viewModel: ViewModel = when {
            modelClass.isAssignableFrom(SessionViewModel::class.java) -> SessionViewModel(c.session)
            modelClass.isAssignableFrom(GardenViewModel::class.java) -> GardenViewModel(c.garden)
            modelClass.isAssignableFrom(ShopViewModel::class.java) -> ShopViewModel(c.garden, c.wallet)
            modelClass.isAssignableFrom(JournalViewModel::class.java) -> JournalViewModel(c.journal)
            modelClass.isAssignableFrom(QuestsViewModel::class.java) -> QuestsViewModel(c.quests)
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
        return viewModel as T
    }
}

@Composable
fun rememberViewModelFactory(): ViewModelProvider.Factory {
    val app = LocalContext.current.applicationContext as ThryviiApplication
    return androidx.compose.runtime.remember(app) { ThryviiViewModelFactory(app) }
}
