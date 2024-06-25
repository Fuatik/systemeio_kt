package io.systeme.test_task_kt.payment

import org.springframework.stereotype.Component

@Component
class Paypal : PaymentProcessor {
    override fun name(): String = "paypal"

    override fun payWithProcessor(price: Double): Boolean {
        return runCatching {
            makePayment(price.toInt())
        }.isSuccess
    }

    private fun makePayment(price: Int) {
        if (price > 100000) {
            throw Exception("Price exceeds the Paypal limit.")
        }
    }
}