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

    @JvmInline
    value class PaymentMethod(val method: String)

    data class Payment(val Method: PaymentMethod)

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
        val sb = Payment(PaymentMethod("dings"))
        val json2 = mapper.writeValueAsString(sb)

        assertThat(json2).isEqualTo("{\"method\":\"dings\"}")
    }

    @Test
    fun `JSON to Strong (Payment)`() {
        val json1 = "{\"method\":\"bla\"}"

        val result =  assertDoesNotThrow { mapper.readValue<PaymentDto>(json1) }

        assertThat(result).isNotNull()
        assertThat(result.method).isEqualTo(PaymentMethod("bla"))
    }
}