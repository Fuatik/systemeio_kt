package io.systeme.test_task_kt.web

import io.systeme.test_task_kt.service.PricingService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class PriceRequest(
   val product: Int,
   val taxNumber: String,
   val couponCode: String?
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