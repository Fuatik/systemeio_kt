package io.systeme.test_task_kt.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "tax_rate")
class TaxRate(
    @Column(name = "region", nullable = false)
    val region: String,
    @Column(name = "rate", nullable = false)
    val rate: Double
) : BaseEntity()
