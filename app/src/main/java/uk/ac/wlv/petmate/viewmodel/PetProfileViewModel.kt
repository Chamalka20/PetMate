package uk.ac.wlv.petmate.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.repository.PetRepository
import uk.ac.wlv.petmate.model.Allergy
import uk.ac.wlv.petmate.model.MedicalCondition
import uk.ac.wlv.petmate.model.Pet
import uk.ac.wlv.petmate.model.PetType
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import uk.ac.wlv.petmate.core.utils.safeApiCall
import uk.ac.wlv.petmate.data.repository.ImageRepository
import kotlin.toString

class PetProfileViewModel(
    private val petRepository: PetRepository,
    private val imageRepository: ImageRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _petListState =
        MutableStateFlow<UiState<List<Pet>>>(UiState.Idle)
    val petListState: StateFlow<UiState<List<Pet>>> =
        _petListState.asStateFlow()

    private val _currentStep = MutableStateFlow(
        savedStateHandle["currentStep"] ?: 0
    )
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _petName = MutableStateFlow(
        savedStateHandle["petName"] ?: ""
    )
    val petName = _petName.asStateFlow()

    private val _petType = MutableStateFlow<PetType?>(
        savedStateHandle.get<String>("petType")?.let { PetType.valueOf(it) }
    )
    val petType = _petType.asStateFlow()

    private val _petAge = MutableStateFlow(
        savedStateHandle["petAge"] ?: 0
    )
    val petAge = _petAge.asStateFlow()

    private val _petImageUri = MutableStateFlow<Uri?>(
        savedStateHandle.get<String>("petImageUri")?.toUri()
    )
    val petImageUri = _petImageUri.asStateFlow()


    private val _isSpayedNeutered = MutableStateFlow<Boolean?>(
        savedStateHandle["isSpayedNeutered"]
    )
    val isSpayedNeutered = _isSpayedNeutered.asStateFlow()

    private val _medicalConditions = MutableStateFlow(
        savedStateHandle.get<ArrayList<String>>("medicalConditions")
            ?.map { id -> MedicalCondition(id = id, name = "") }
            ?: emptyList()
    )
    val medicalConditions: StateFlow<List<MedicalCondition>> =
        _medicalConditions.asStateFlow()

    private val _allergies = MutableStateFlow(
        savedStateHandle.get<ArrayList<String>>("allergies")
            ?.map { id -> Allergy(id = id, name = "") }
            ?: emptyList()
    )
    val allergies: StateFlow<List<Allergy>> = _allergies.asStateFlow()

    private val _savePetState = MutableStateFlow<UiState<Pet>>(UiState.Idle)
    val savePetState: StateFlow<UiState<Pet>> = _savePetState.asStateFlow()

    init {
        loadPetList()
        loadMedicalConditions()
        loadAllergies()
    }

    fun loadPetList() {
        viewModelScope.launch {
            _petListState.value = UiState.Loading

            val result = safeApiCall {
                petRepository.getPetList()
            }

            result.onSuccess { pets ->
                _petListState.value = UiState.Success(pets)
            }.onFailure { exception ->
                _petListState.value = UiState.Error(
                    exception.message ?: "Failed to load pets"
                )
            }
        }
    }

    fun updatePetName(name: String) {
        _petName.value = name
        savedStateHandle["petName"] = name
    }

    fun updatePetType(type: PetType) {
        _petType.value = type
        savedStateHandle["petType"] = type.name

    }

    fun updatePetAge(age: Int) {
        _petAge.value = age
        savedStateHandle["petAge"] = age
    }
    fun updatePetImage(uri: Uri) {
        _petImageUri.value = uri
        savedStateHandle["petImageUri"] = uri.toString()
    }
    fun removePetImage() {
        _petImageUri.value = null
        savedStateHandle["petImageUri"] =null

    }
    fun updateSpayedNeutered(value: Boolean) {
        _isSpayedNeutered.value = value
        savedStateHandle["isSpayedNeutered"] = value
    }

    fun toggleMedicalCondition(conditionId: String) {
        val updatedList = _medicalConditions.value.map {
            if (it.id == conditionId)
                it.copy(isSelected = !it.isSelected)
            else it
        }

        _medicalConditions.value = updatedList

        savedStateHandle["medicalConditions"] =
            ArrayList(
                updatedList
                    .filter { it.isSelected }
                    .map { it.id }
            )


    }

    fun toggleAllergy(allergyId: String) {
        val updatedList = _allergies.value.map {
            if (it.id == allergyId)
                it.copy(isSelected = !it.isSelected)
            else it
        }

        _allergies.value = updatedList

        savedStateHandle["allergies"] =
            ArrayList(
                updatedList
                    .filter { it.isSelected }
                    .map { it.id }
            )
    }

    fun nextStep() {
        if (validateCurrentStep()) {
            _currentStep.value += 1
            savedStateHandle["currentStep"] = _currentStep.value
        }
    }

    fun previousStep() {
        if (_currentStep.value > 0) {
            _currentStep.value -= 1
            savedStateHandle["currentStep"] = _currentStep.value
        }
    }

    fun skipStep() {
        _currentStep.value += 1
    }

    private fun validateCurrentStep(): Boolean {
        return when (_currentStep.value) {
            0 -> {
                if (_petName.value.isBlank()) {
                    showError("Please enter your pet's name")

                    false
                } else true
            }
            1->{
                // Age validation - can be 0 for "less than 1 year"
                true
            }
            2 -> true
            3 -> {
                if (_isSpayedNeutered.value == null) {
                    showError("Please select an option")
                    false
                } else true
            }
            else -> true
        }
    }

    fun savePet( ) {
        if (_petType.value == null) {
            showError("Please select Pet Type")
            return
        }
        checkInternetAndExecute(
            onConnected = {
                viewModelScope.launch {
                    _savePetState.value = UiState.Loading
                    var imageUrl = ""
                    _petImageUri.value?.let { uri ->

                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                                ?: throw IllegalStateException("User not logged in")
                            val folder = "$userId/pets/profile"
                            val uploadResult = safeApiCall {
                                imageRepository.uploadImage(uri,folder)
                            }

                            uploadResult.onSuccess { url ->
                                imageUrl = url
                            }

                        }

                    val pet = Pet(
                        name = _petName.value,
                        type = _petType.value,
                        age = _petAge.value,
                        imageUrl = imageUrl,
                        isSpayedNeutered = _isSpayedNeutered.value ?: false,
                        medicalConditions = _medicalConditions.value
                            .filter { it.isSelected }
                            .map { it.name },
                        allergies = _allergies.value
                            .filter { it.isSelected }
                            .map { it.name }
                    )

                    val result = safeApiCall {
                        petRepository.savePet(pet)
                    }

                    result.onSuccess {
                        _savePetState.value = UiState.Success(pet)
                        showSuccess("Pet profile created successfully!")
                    }.onFailure { exception ->
                        val message = exception.message ?: "Failed to save pet"
                        showError(message)
                        _savePetState.value = UiState.Error(message)
                    }

                }
            }
        )
    }

    private fun loadMedicalConditions() {
        _medicalConditions.value = listOf(
            MedicalCondition("1", "Diabetes"),
            MedicalCondition("2", "Kidney Disease"),
            MedicalCondition("3", "Heart Disease"),
            MedicalCondition("4", "Arthritis"),
            MedicalCondition("5", "Obesity"),
            MedicalCondition("6", "Other")
        )
    }

    private fun loadAllergies() {
        _allergies.value = listOf(
            Allergy("1", "Pollen"),
            Allergy("2", "Dust"),
            Allergy("3", "Mold"),
            Allergy("4", "Protein"),
            Allergy("5", "Dairy"),
            Allergy("6", "Other")
        )
    }
}