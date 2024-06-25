package io.systeme.test_task_kt.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.every
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
internal class CalculatePriceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockK
    private lateinit var pricingService: PricingService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should calculate price with valid request`() {
        val productId = 1
        val taxNumber = "IT12345678900"
        val couponCode = "D15"
        val totalPrice = 103.7

        every { pricingService.calculateTotalPrice(productId, taxNumber, couponCode) } returns totalPrice

        val requestJson = """
            {
                "product": $productId,
                "taxNumber": "$taxNumber",
                "couponCode": "$couponCode"
            }
        """

        mockMvc.perform(
            post("/calculate-price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.request.product").value(productId))
            .andExpect(jsonPath("$.request.taxNumber").value(taxNumber))
            .andExpect(jsonPath("$.request.couponCode").value(couponCode))
            .andExpect(jsonPath("$.totalPrice").value(totalPrice))

    }

    @Test
    fun `should return 400 Bad Request on invalid product id`() {
        val invalidRequest = """
            {
                "product": 100,
                "taxNumber": "IT12345678900",
                "couponCode": "D15"
            }
        """

        mockMvc.perform(
            post("/calculate-price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
            .andExpect {
                val problemDetail = ObjectMapper().readValue(it.response.contentAsString, ProblemDetail::class.java)
                assert((problemDetail.detail?.contains("Product not found"))!!)
            }
    }

    @Test
    fun `should return 400 Bad Request on invalid tax number`() {
        val invalidRequest = """
            {
                "product": 1,
                "taxNumber": "IT123456900",
                "couponCode": "D15"
            }
        """

        mockMvc.perform(
            post("/calculate-price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
            .andExpect {
                val problemDetail = ObjectMapper().readValue(it.response.contentAsString, ProblemDetail::class.java)
                assert((problemDetail.detail?.contains("Invalid tax number"))!!)
            }
    }

    @Test
    fun `should return 400 Bad Request on invalid coupon code`() {
        val invalidRequest = """
            {
                "product": 1,
                "taxNumber": "IT12345678900",
                "couponCode": "Y15"
            }
        """

        mockMvc.perform(
            post("/calculate-price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
            .andExpect {
                val problemDetail = ObjectMapper().readValue(it.response.contentAsString, ProblemDetail::class.java)
                assert((problemDetail.detail?.contains("Invalid coupon code"))!!)
            }
    }

    @Test
    fun `should return 400 Bad Request doe to negative price after applying coupon`() {
        val productId = 3
        val taxNumber = "IT12345678900"
        val couponCode = "F50"

        every {
            pricingService.calculateTotalPrice(productId, taxNumber, couponCode)
        } throws IllegalArgumentException("Price must be positive")

        val invalidRequest = """
            {
                "product": $productId,
                "taxNumber": "$taxNumber",
                "couponCode": "$couponCode"
            }
        """

        mockMvc.perform(
            post("/calculate-price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
            .andExpect {
                val problemDetail = ObjectMapper().readValue(it.response.contentAsString, ProblemDetail::class.java)
                assert((problemDetail.detail?.contains("Price must be positive"))!!)
            }
    }
}