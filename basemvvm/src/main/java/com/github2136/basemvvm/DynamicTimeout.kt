package com.github2136.basemvvm

/**
 * Created by 44569 on 2023/4/27
 * 动态超时设置
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@kotlin.annotation.Target(AnnotationTarget.FUNCTION)
annotation class DynamicTimeout(val timeout: Int)