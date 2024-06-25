package io.systeme.test_task_kt.service

import io.systeme.test_task_kt.payment.PaymentProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PaymentProcessorFactory @Autowired constructor (processorsList: List<PaymentProcessor>) {
    private val processors: Map<String, PaymentProcessor> = processorsList.associateBy { it.name().lowercase() }

    fun getPaymentProcessor(name: String): PaymentProcessor? = processors[name.lowercase()]
}