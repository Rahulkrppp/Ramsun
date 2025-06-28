package de.fast2work.mobility.data.response

data class Properties(
    val birth_date: BirthDate = BirthDate(),
    val first_name: FirstName = FirstName(),
    val last_name: LastName = LastName()
)