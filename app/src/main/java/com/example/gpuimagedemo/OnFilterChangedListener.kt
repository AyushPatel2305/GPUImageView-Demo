package com.example.gpuimagedemo

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup

interface OnFilterChangedListener {
    fun onFilterChanged(filter: GPUImageFilterGroup)
}