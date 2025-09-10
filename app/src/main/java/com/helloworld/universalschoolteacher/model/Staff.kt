package com.helloworld.universalschoolteacher.model

data class Staff(
    var Username: String? = null,
    var Password: String? = null,
    var manages: String? = null,
    var isActive: Boolean? = null
)
