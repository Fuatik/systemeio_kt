package io.systeme.test_task_kt.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // https://stackoverflow.com/a/28025008/548473
    val id: Int? = null

    //    https://stackoverflow.com/questions/1638723
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is BaseEntity) {
            return false
        }
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }

    override fun toString(): String {
        return "${javaClass.simpleName}:$id"
    }
}