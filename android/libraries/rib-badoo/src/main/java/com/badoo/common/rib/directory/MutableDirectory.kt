package com.badoo.common.rib.directory

import kotlin.reflect.KClass

interface MutableDirectory : Directory {

    fun <T : Any> put(key: KClass<T>, value: T)
}
