package io.systeme.test_task_kt.payment

interface PaymentProcessor {
    fun name(): String
    fun payWithProcessor(price: Double): Boolean
}