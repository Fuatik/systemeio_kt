package io.systeme.test_task_kt.service

import io.systeme.test_task_kt.exception.BadRequestException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PaymentService(private val paymentProcessorFactory: PaymentProcessorFactory) {

    @Transactional
    fun payWithProcessor(totalPrice: Double, processorName: String) {
        val processor = paymentProcessorFactory.getPaymentProcessor(processorName)
            ?: throw BadRequestException("Payment processor not found")

        if (!processor.payWithProcessor(totalPrice)) {
            throw BadRequestException("Payment failed")
        }
    }
}