package io.systeme.test_task_kt.repository

import io.systeme.test_task_kt.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository : JpaRepository<Coupon, Int> {
    fun findByCode(code: String): Coupon?
}