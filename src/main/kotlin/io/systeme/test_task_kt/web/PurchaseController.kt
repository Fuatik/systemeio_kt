package io.systeme.test_task_kt.web

import io.systeme.test_task_kt.service.PaymentService
import io.systeme.test_task_kt.service.PricingService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class PurchaseRequest(
    val product: Int,
    val taxNumber: String,
    val couponCode: String?,
    val paymentProcessor: String
)

data class PurchaseResponse(
    val request: PurchaseRequest,
    val totalPrice: Double,
    val message: String
)

@RestController
class PurchaseController(
    private val pricingService: PricingService,
    private val paymentService: PaymentService
) {
    @PostMapping("/purchase")
    fun purchase(@RequestBody @Validated request: PurchaseRequest): ResponseEntity<PurchaseResponse> {
        val totalPrice = pricingService.calculateTotalPrice(request.product, request.taxNumber, request.couponCode)

        paymentService.payWithProcessor(totalPrice, request.paymentProcessor)

        val response = PurchaseResponse(request, totalPrice, "Payment successful")

        return ResponseEntity.ok(response)
    }
}