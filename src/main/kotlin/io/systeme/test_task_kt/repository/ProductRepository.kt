package io.systeme.test_task_kt.repository

import io.systeme.test_task_kt.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Int>