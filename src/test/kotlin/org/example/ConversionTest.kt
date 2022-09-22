package org.example

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ConversionTest {
    val mapper = jacksonObjectMapper().findAndRegisterModules()

    data class StringBean(
        val property1: String
    )

    @JvmInline
    value class StringType(val value: String)

    data class StrongBean(val property1: StringType)

    data class StrongBeanDTO(val property1: StringType) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun create(s: String) = StrongBeanDTO(StringType(s))
        }
    }

    @JvmInline
    value class PaymentMethod(val method: String)

    data class Payment(val method: PaymentMethod) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun create(value: String) = Payment(PaymentMethod(value))
        }
    }

    data class PaymentDto(val method: PaymentMethod) {
        companion object {
            @JvmStatic
            @JsonCreator
            fun create(method: String) = PaymentDto(PaymentMethod(method))
        }
    }

    @Test
    fun `String to JSON`() {
        val sb = StringBean("foo")

        val json: String = mapper.writeValueAsString(sb)

        assertThat(json).isEqualTo("{\"property1\":\"foo\"}")
    }

    @Test
    fun `JSON to String`() {
        val json = """
            { "property1": "bar" }
        """.trimIndent()

        val sb: StringBean = mapper.readValue<StringBean>(json)
    }

    @Test
    fun `Strong to JSON`() {
        val sb = StrongBean(StringType("dings"))
        val json = mapper.writeValueAsString(sb)
        assertThat(json).isEqualTo("{\"property1\":\"dings\"}")

        val payment = Payment(PaymentMethod("PAYPAL"))
        val json2 = mapper.writeValueAsString(payment)

        assertThat(json2).isEqualTo("{\"method\":\"PAYPAL\"}")
    }

    @Test
    fun `JSON to Strong (Payment)`() {
        val json = "{\"method\":\"PAYPAL\"}"

        val result =  assertDoesNotThrow { mapper.readValue<PaymentDto>(json) }

        assertThat(result).isNotNull()
        assertThat(result.method).isEqualTo(PaymentMethod("PAYPAL"))
    }
}