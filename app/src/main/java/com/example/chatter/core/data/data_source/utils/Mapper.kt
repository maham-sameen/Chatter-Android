package com.example.chatter.core.data.data_source.utils

interface Mapper<F , T> {
    fun map(from : F) : T
}