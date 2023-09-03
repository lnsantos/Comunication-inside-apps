package com.lnsantos.clientapp.model

import androidx.compose.ui.graphics.Color
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import java.util.Locale.getDefault
import java.util.UUID

data class ProductItemUI(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: BigDecimal,
    var isSelected: Boolean = false
) {

    fun getPriceFormatted() : String {
        return NumberFormat.getCurrencyInstance(getDefault()).format(price)
    }

    fun getNameFormatted() : String {
        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    fun getDescriptionShop() = if (isSelected) { "No carrinho" } else { "" }

    fun getColorShop() = if (isSelected) { Color.Green } else { Color.Transparent }
}
