package io.systeme.test_task_kt.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Range

@Entity
@Table(name = "product")
class Product(
    @NotBlank
    @Size(min = 2, max = 128)
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "price", nullable = false)
    @Range(min = 0)
    val price: Double
) : BaseEntity()
