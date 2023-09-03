package com.lnsantos.clientapp.mock

import com.lnsantos.clientapp.model.ProductItemUI
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

internal class MockkProducts {

    val memory = arrayListOf(
        ProductItemUI(
            name = "papel",
            price = BigDecimal(156780)
        ),
        ProductItemUI(
            name = "pedra",
            price = BigDecimal(234)
        ),
        ProductItemUI(
            name = "bolsa",
            price = BigDecimal(1234)
        ),
        ProductItemUI(
            name = "caneta",
            price = BigDecimal(150)
        ),
        ProductItemUI(
            name = "joias",
            price = BigDecimal(89058670)
        )
    )

    fun update(item:ProductItemUI) : Int {
        memory.forEach {
            if (it.id == item.id) {
                it.isSelected = !it.isSelected
            }
        }
        return memory.filter { it.isSelected }.size
    }

    fun getTotalFormatted() : String {
        val total = BigDecimal(0)
        memory.filter { it.isSelected }.forEach { total.plus(it.price) }
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(total)
    }
    fun getTotal() : BigDecimal {
        val total = BigDecimal(0)
        memory.filter { it.isSelected }.forEach { total.plus(it.price) }
        return total
    }
}
