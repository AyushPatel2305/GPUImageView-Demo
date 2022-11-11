package com.example.gpuimagedemo.utils

import java.util.*
import kotlin.collections.HashMap

class FilterList {
    private val filters: MutableMap<String, FilterType> = HashMap()

    fun addFilter(name: String, filter: FilterType) {
        filters[name] = filter
    }

    fun getSize(): Int {
        return filters.size
    }

    fun getNames(): MutableSet<String> {
        return filters.keys
    }

    fun getFilter(name: String): FilterType {
        return filters[name]!!
    }
}
