package uk.ac.wlv.petmate.model

data class Pet(
    val id: String = "",
    val name: String = "",
    var type: PetType? = null,
    val breed: String = "",
    val age: Int = 0,
    val isSpayedNeutered: Boolean = false,
    val medicalConditions: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val imageUrl: String = ""
)

enum class PetType {
    CAT, DOG,BIRD,RABBIT,HAMSTER,FISH,REPTILE
}

data class MedicalCondition(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

data class Allergy(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)