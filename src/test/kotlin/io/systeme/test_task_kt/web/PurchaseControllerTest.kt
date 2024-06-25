package io.systeme.test_task_kt.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.systeme.test_task_kt.service.PricingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
internal class PurchaseControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockK
    private lateinit var pricingService: PricingService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should make successful purchase with valid request`() {
        val productId = 1
        val taxNumber = "IT12345678900"
        val couponCode = "D15"
        val paymentProcessor = "paypal"
        val totalPrice = 103.7

        coEvery { pricingService.calculateTotalPrice(productId, taxNumber, couponCode) } returns totalPrice

        val requestJson = """
            {
                "product": $productId,
                "taxNumber": "$taxNumber",
                "couponCode": "$couponCode",
                "paymentProcessor": "$paymentProcessor"
            }
        """

        mockMvc.perform(
            post("/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.request.product").value(productId))
            .andExpect(jsonPath("$.request.taxNumber").value(taxNumber))
            .andExpect(jsonPath("$.request.couponCode").value(couponCode))
            .andExpect(jsonPath("$.totalPrice").value(totalPrice))
            .andExpect(jsonPath("$.message").value("Payment successful"))

    }

    @Test
    fun `should return 400 Bad Request on invalid paymentProcessor`() {
        val productId = 1
        val taxNumber = "IT12345678900"
        val couponCode = "D15"
        val paymentProcessor = "invalid_processor"
        val totalPrice = 103.7

        coEvery { pricingService.calculateTotalPrice(productId, taxNumber, couponCode) } returns totalPrice

        val requestJson = """
            {
                "product": $productId,
                "taxNumber": "$taxNumber",
                "couponCode": "$couponCode",
                "paymentProcessor": "$paymentProcessor"
            }
        """

        mockMvc.perform(
            post("/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect {
                val problemDetail = ObjectMapper().readValue(it.response.contentAsString, ProblemDetail::class.java)
                assert((problemDetail.detail?.contains("Invalid payment processor"))!!)
            }
    }
}