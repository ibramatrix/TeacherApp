package com.helloworld.universalschoolteacher.model

import com.google.firebase.database.PropertyName

data class Diary(
    @get:PropertyName("ClassName") @set:PropertyName("ClassName") var ClassName: String? = "",
    @get:PropertyName("Date") @set:PropertyName("Date") var Date: String? = "",
    @get:PropertyName("Text") @set:PropertyName("Text") var Text: String? = "",
    @get:PropertyName("Images") @set:PropertyName("Images") var Images: List<String>? = listOf()
)