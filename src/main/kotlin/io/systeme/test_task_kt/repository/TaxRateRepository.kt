package io.systeme.test_task_kt.repository

import io.systeme.test_task_kt.entity.TaxRate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TaxRateRepository : JpaRepository<TaxRate, Int> {

    @Query("SELECT t FROM TaxRate t WHERE t.region = :region")
    fun findByRegion(region: String): TaxRate?
}