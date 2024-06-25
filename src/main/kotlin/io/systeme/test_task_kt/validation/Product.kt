package io.systeme.test_task_kt.validation

import io.systeme.test_task_kt.repository.ProductRepository
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ProductValidator::class])
annotation class Product(
    val message: String = "Product not found",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)

class ProductValidator : ConstraintValidator<Product, Int?> {

    @Autowired
    private lateinit var productRepository: ProductRepository

    override fun isValid(productId: Int?, context: ConstraintValidatorContext): Boolean {
        if (productId == null) {
            return true
        }

        if (!productRepository.existsById(productId)) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("productId")
                .addConstraintViolation()
            return false
        }

        return true
    }
}