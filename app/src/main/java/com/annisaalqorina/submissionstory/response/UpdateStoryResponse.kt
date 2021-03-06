package com.annisaalqorina.submissionstory.response

import com.google.gson.annotations.SerializedName

data class UpdateStoryResponse(
    @field:SerializedName ("error")
    val error : Boolean? = null,

    @field:SerializedName("message")
    val message : String? = null
)
