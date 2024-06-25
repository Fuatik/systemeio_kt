package io.systeme.test_task_kt.validation

import io.systeme.test_task_kt.service.PaymentProcessorFactory
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PaymentProcessorValidator::class])
annotation class PaymentProcessor(
    val message: String = "Invalid payment processor",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PaymentProcessorValidator : ConstraintValidator<PaymentProcessor, String> {

    @Autowired
    private lateinit var paymentProcessorFactory: PaymentProcessorFactory

    override fun initialize(constraintAnnotation: PaymentProcessor) {}

    override fun isValid(paymentProcessor: String?, context: ConstraintValidatorContext): Boolean {
        if (paymentProcessor == null) {
            return false
        }

        if (paymentProcessorFactory.getPaymentProcessor(paymentProcessor) == null) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("paymentProcessor")
                .addConstraintViolation()
            return false
        }

        return true
    }
}