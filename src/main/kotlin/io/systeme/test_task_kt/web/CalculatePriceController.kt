package io.systeme.test_task_kt.web

import io.systeme.test_task_kt.service.PricingService
import io.systeme.test_task_kt.validation.CouponCode
import io.systeme.test_task_kt.validation.TaxNumber
import io.systeme.test_task_kt.validation.Product
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class PriceRequest(
    @NotNull @Product val product: Int,
    @NotBlank @TaxNumber val taxNumber: String,
    @CouponCode val couponCode: String?
)

data class PriceResponse(
    val request: PriceRequest,
    val totalPrice: Double
)

@RestController
class CalculatePriceController(private val pricingService: PricingService) {
    @PostMapping("/calculate-price")
    fun calculatePrice(@RequestBody @Validated request: PriceRequest): ResponseEntity<PriceResponse> {
        val totalPrice = pricingService.calculateTotalPrice(request.product, request.taxNumber, request.couponCode)

        val response = PriceResponse(request, totalPrice)

        return ResponseEntity.ok(response)
    }
}