package com.back.global.jpa.domain

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class AfterDDL(
    val sql: String,
)
