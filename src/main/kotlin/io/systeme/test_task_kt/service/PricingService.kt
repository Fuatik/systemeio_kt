package io.systeme.test_task_kt.service

import io.systeme.test_task_kt.entity.Coupon
import io.systeme.test_task_kt.entity.Product
import io.systeme.test_task_kt.exception.BadRequestException
import io.systeme.test_task_kt.repository.CouponRepository
import io.systeme.test_task_kt.repository.ProductRepository
import io.systeme.test_task_kt.repository.TaxRateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PricingService(
    private val productRepository: ProductRepository,
    private val couponRepository: CouponRepository,
    private val taxRateRepository: TaxRateRepository
) {

    @Transactional
    fun calculateTotalPrice(productId: Int, taxNumber: String, couponCode: String?): Double {
        val product: Product = productRepository.findById(productId).orElseThrow {
            BadRequestException("Product with id $productId not found")
        }

        val coupon = couponCode?.let(couponRepository::findByCode)

        val price = product.price
        val discountedPrice = coupon?.let { applyCouponDiscount(price, it) } ?: price

        val taxRate = getTaxRateForTaxNumber(taxNumber)

        return discountedPrice * (1 + taxRate)
    }

    private fun applyCouponDiscount(price: Double, coupon: Coupon): Double {
        val discount = coupon.discount
        val discountedPrice = if (coupon.isPercentage) price * (1 - discount / 100) else price - discount

        require(discount >= 0) { "Price must be positive" }

        return discountedPrice
    }

    private fun getTaxRateForTaxNumber(taxNumber: String): Double =
        taxRateRepository.findByRegion(taxNumber.take(2))?.rate
            ?: throw BadRequestException("Invalid tax number: $taxNumber")

}