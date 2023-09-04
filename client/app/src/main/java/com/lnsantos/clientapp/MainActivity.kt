package com.lnsantos.clientapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.lnsantos.bank.ServerBankPaymentidlInterface
import com.lnsantos.clientapp.mock.MockkProducts
import com.lnsantos.clientapp.model.ProductItemUI
import com.lnsantos.clientapp.ui.theme.ClientAppTheme
import java.math.BigDecimal
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity(), ServiceConnection {

    private val mock = MockkProducts()

    @SuppressLint("MutableCollectionMutableState", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClientAppTheme {
                val shopQuantity = remember { mutableStateOf(0) }
                val items = remember { mutableStateOf(mock.memory) }
                val columnState = rememberLazyListState()
                val isFinished = remember { mutableStateOf(false) }
                val context = LocalContext.current

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    color = MaterialTheme.colorScheme.background
                ) {

                    if (isFinished.value) {
                        Text(text = "Compra finalizada com sucesso")
                        return@Surface
                    }

                    Scaffold(
                        modifier = Modifier.padding(18.dp),
                        topBar = {
                            TopAppBar(
                                title = { Text(text = "Client Store") },
                                modifier = Modifier.fillMaxWidth(),
                                actions = {
                                    Button(
                                        onClick = {
                                            val intent = Intent().apply {
                                                component = ComponentName(
                                                    "com.lnsantos.server",
                                                    "com.lnsantos.server.service.BankPaymentService"
                                                )
                                            }

                                            val callbackBind = object : ServiceConnection {
                                                override fun onServiceConnected(
                                                    name: ComponentName?,
                                                    service: IBinder?
                                                ) {
                                                    val serviceBank = ServerBankPaymentidlInterface
                                                        .Stub
                                                        .asInterface(service)

                                                    serviceBank.send(
                                                        mock.getTotal().longValueExact(),
                                                        UUID.randomUUID().toString(),
                                                        "client_app"
                                                    )
                                                }

                                                override fun onServiceDisconnected(name: ComponentName?) {
                                                    // TODO
                                                }
                                            }

                                            context.bindService(
                                                intent,
                                                callbackBind,
                                                Context.BIND_AUTO_CREATE
                                            )
                                        },
                                        enabled = shopQuantity.value != 0
                                    ) {
                                        val description = "Pagar"
                                        val btn = if (shopQuantity.value == 0) {
                                            description
                                        } else {
                                            String.format(
                                                "%s %s items",
                                                description,
                                                shopQuantity.value
                                            )
                                        }
                                        Text(text = btn)
                                    }
                                }
                            )
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = if (shopQuantity.value == 0) {
                                        it.calculateTopPadding()
                                    } else {
                                        it.calculateTopPadding() + Dp(20f)
                                    }
                                )
                                .animateContentSize()
                        ) {

                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                state = columnState
                            ) {
                                itemsIndexed(
                                    items.value,
                                    key = { i, item -> item.id }) { index, product ->
                                    ProductItem(product) { productTouch ->
                                        // items.find { it == productTouch }?.let { it.isSelected = !it.isSelected }
                                        shopQuantity.value = mock.update(productTouch)
                                        items.value = mock.memory
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        TODO("Not yet implemented")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        TODO("Not yet implemented")
    }
}


@Composable
fun ProductItem(
    product: ProductItemUI,
    selected: (ProductItemUI) -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.clickable { selected(product) }
    ) {
        Text(
            text = product.getDescriptionShop(),
            fontStyle = FontStyle.Normal,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(12.dp),
            color = product.getColorShop()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(18f))
                .padding(12.dp)
        ) {
            Text(
                text = product.getNameFormatted(),
                fontStyle = FontStyle.Normal,
                fontSize = TextUnit(18f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.getPriceFormatted()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClientAppTheme {
        ProductItem(
            ProductItemUI(
                id = UUID.randomUUID().toString(),
                name = "papel",
                price = BigDecimal(16009),
                isSelected = true
            )
        )
    }
}