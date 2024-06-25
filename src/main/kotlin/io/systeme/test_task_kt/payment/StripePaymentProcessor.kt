package io.systeme.test_task_kt.payment

import org.springframework.stereotype.Component

@Component
class StripePaymentProcessor : PaymentProcessor {
    override fun name(): String = "stripe"

    override fun payWithProcessor(price: Double): Boolean = pay(price.toFloat())

    private fun pay(price: Float): Boolean = price >= 100
}