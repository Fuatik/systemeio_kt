package io.systeme.test_task_kt.validation

import io.systeme.test_task_kt.validation.tax.TaxNumberValidatorRegistry
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [TaxNumberValidator::class])
annotation class TaxNumber(
    val message: String = "Invalid tax number",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)

class TaxNumberValidator : ConstraintValidator<TaxNumber, String> {

    @Autowired
    private lateinit var taxNumberValidatorRegistry: TaxNumberValidatorRegistry

    override fun initialize(constraintAnnotation: TaxNumber) {}

    override fun isValid(taxNumber: String?, context: ConstraintValidatorContext): Boolean {
        if (taxNumber == null) {
            return false
        }

        val countryCode = taxNumber.substring(0, 2)
        val validator = taxNumberValidatorRegistry.getValidator(countryCode)

        if (validator == null || !validator.isValid(taxNumber)) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("taxNumber")
                .addConstraintViolation()
            return false
        }

        return true
    }
}
