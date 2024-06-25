package io.systeme.test_task_kt.validation.tax

import org.springframework.stereotype.Component
import java.util.regex.Pattern

interface TaxNumberValidatorStrategy {
    fun isValid(taxNumber: String): Boolean
}

@Component
class TaxNumberValidatorRegistry {
    private val validators: MutableMap<String, TaxNumberValidatorStrategy> = HashMap()

    init {
        registerValidator("DE", GermanyTaxNumberValidator())
        registerValidator("IT", ItalyTaxNumberValidator())
        registerValidator("GR", GreeceTaxNumberValidator())
        registerValidator("FR", FranceTaxNumberValidator())
    }

    private fun registerValidator(countryCode: String, validator: TaxNumberValidatorStrategy) {
        validators[countryCode] = validator
    }

    fun getValidator(countryCode: String): TaxNumberValidatorStrategy? {
        return validators[countryCode]
    }
}

class GermanyTaxNumberValidator : TaxNumberValidatorStrategy {
    private val DE_PATTERN: Pattern = Pattern.compile("DE[0-9]{9}")

    override fun isValid(taxNumber: String): Boolean {
        return DE_PATTERN.matcher(taxNumber).matches()
    }
}

class ItalyTaxNumberValidator : TaxNumberValidatorStrategy {
    private val IT_PATTERN: Pattern = Pattern.compile("IT[0-9]{11}")

    override fun isValid(taxNumber: String): Boolean {
        return IT_PATTERN.matcher(taxNumber).matches()
    }
}

class GreeceTaxNumberValidator : TaxNumberValidatorStrategy {
    private val GR_PATTERN: Pattern = Pattern.compile("GR[0-9]{9}")

    override fun isValid(taxNumber: String): Boolean {
        return GR_PATTERN.matcher(taxNumber).matches()
    }
}

class FranceTaxNumberValidator : TaxNumberValidatorStrategy {
    private val FR_PATTERN: Pattern = Pattern.compile("FR[A-Z]{2}[0-9]{9}")

    override fun isValid(taxNumber: String): Boolean {
        return FR_PATTERN.matcher(taxNumber).matches()
    }
}