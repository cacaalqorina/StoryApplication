package com.annisaalqorina.submissionstory.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetAllStoryResponse(
    @field:SerializedName("error")
    val error : Boolean? = null,

    @field:SerializedName("message")
    val message : String? = null,

    @field:SerializedName("listStory")
    val listStory : List<ListStoryItem> ? = null
)

@Parcelize
data class ListStoryItem (
    @field:SerializedName("id")
    val id : String? = null,

    @field:SerializedName("name")
    val name : String? = null,

    @field:SerializedName("description")
    val description : String? = null,

    @field:SerializedName("photoUrl")
    val photoUrl : String? = null,

    @field:SerializedName("createdAt")
    val createdAt : String? = null
) : Parcelable
