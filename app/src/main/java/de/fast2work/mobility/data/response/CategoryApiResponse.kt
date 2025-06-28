package de.fast2work.mobility.data.response


import com.google.gson.annotations.SerializedName

data class CategoryApiResponse(
    @SerializedName("data")
    val data: ArrayList<Category>? = arrayListOf()
){
    data class Category(
        @SerializedName("categoryIcon")
        val categoryIcon: String? = "",
        @SerializedName("categoryId")
        val categoryId: Int? = 0,
        @SerializedName("categoryName")
        val categoryName: String? = "",
        @SerializedName("createdAt")
        val createdAt: String? = "",
        @SerializedName("currencyId")
        val currencyId: Int? = 0,
        @SerializedName("linkedSubCatagoriesCount")
        val linkedSubCatagoriesCount: Int? = 0,
        @SerializedName("linkedSubCatagoriesId")
        val linkedSubCatagoriesId: List<Int?>? = listOf(),
        @SerializedName("status")
        val status: String? = "",
        @SerializedName("systemCode")
        val systemCode: String? = "",
        @SerializedName("updatedAt")
        val updatedAt: String? = "",
        var isSelected: Boolean = false
    )
}