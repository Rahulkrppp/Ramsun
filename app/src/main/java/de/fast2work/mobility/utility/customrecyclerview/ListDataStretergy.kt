package com.app.knit.utility.customrecyclerview

data class ListData<T>(val data: ArrayList<T>, val dataStrategy: ListDataStrategy, val message: String? = "")

enum class ListDataStrategy {
    ADD,
    CLEAR,
    CLEAR_ADD
}