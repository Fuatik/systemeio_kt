package io.systeme.test_task_kt.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.Pattern

@Entity
@Table(name = "coupon")
class Coupon(
    @Column(name = "code", nullable = false)
    @Pattern(regexp = "[A-Z][0-9]{2}", message = "Strict coupon format: uppercase letter and two digits")
    val code: String,

    @Column(name = "discount", nullable = false)
    val discount: Double,

    @Column(name = "is_percentage", nullable = false)
    val isPercentage: Boolean
) : BaseEntity()