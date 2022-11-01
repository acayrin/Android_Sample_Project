package me.acayrin.sampleproject.database.model

class Rental(
	val id: Int,
	val id_librarian: Int,
	val id_member: Int,
	val id_book: Int,
	val date_start: String,
	val status: Int,
	val date_end: String
)