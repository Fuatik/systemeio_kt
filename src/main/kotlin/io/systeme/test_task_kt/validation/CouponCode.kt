package io.systeme.test_task_kt.validation

import io.systeme.test_task_kt.repository.CouponRepository
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CouponCodeValidator::class])
annotation class CouponCode(
    val message: String = "Invalid coupon code",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class CouponCodeValidator : ConstraintValidator<CouponCode, String> {

    private val COUPON_PATTERN = Regex("[A-Z][0-9]{2}")

    @Autowired
    private lateinit var couponRepository: CouponRepository // Предполагается, что у вас есть CouponRepository

    override fun initialize(constraintAnnotation: CouponCode) {}

    override fun isValid(couponCode: String?, context: ConstraintValidatorContext): Boolean {
        if (couponCode == null) {
            return true // Разрешаем null значения, используйте @NotNull для строгой валидации non-null значений
        }

        if (!couponCode.matches(COUPON_PATTERN) || couponRepository.findByCode(couponCode) == null) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("couponCode")
                .addConstraintViolation()
            return false
        }

        return true
    }
}
