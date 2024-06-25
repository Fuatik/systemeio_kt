package io.systeme.test_task_kt.web

import io.systeme.test_task_kt.service.PaymentService
import io.systeme.test_task_kt.service.PricingService
import io.systeme.test_task_kt.validation.CouponCode
import io.systeme.test_task_kt.validation.PaymentProcessor
import io.systeme.test_task_kt.validation.Product
import io.systeme.test_task_kt.validation.TaxNumber
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class PurchaseRequest(
    @NotNull @Product val product: Int,
    @NotBlank @TaxNumber val taxNumber: String,
    @CouponCode val couponCode: String?,
    @NotBlank @PaymentProcessor val paymentProcessor: String
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