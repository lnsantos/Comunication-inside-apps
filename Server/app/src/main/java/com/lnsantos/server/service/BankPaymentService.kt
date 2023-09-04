package com.lnsantos.server.service

import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.IBinder
import android.util.Log
import com.lnsantos.bank.ServerBankPaymentidlInterface
import com.lnsantos.server.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.NumberFormat

interface BankPaymentServiceCallback {
    fun onWaning(code: Int)
}

class BankPaymentService : Service(), BankPaymentServiceCallback {

    private var running: Boolean = true
    private var result: Int = -1

    companion object {
        var callback: BankPaymentServiceCallback? = null
        var resultAction = 0
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBind(intent: Intent?): IBinder? {
        Log.d("server", "initializado onBind")
        if (callback == null) {
            callback = this
        }

        val isLogger: Boolean = true
        val permissionList = arrayOf("client_app")

        return object : ServerBankPaymentidlInterface.Stub() {
            override fun send(
                price: Long,
                clientRequest: String?,
                identification: String?
            ): Int {
                Log.d("server", "initializado")
                return if (isLogger && permissionList.contains(identification)) {
                    Log.d("server", "initializado sucesso")
                    var initialize = false
                    while (resultAction == 0) {
                        if (!initialize) {
                            val total = NumberFormat.getInstance().format(price)
                            val i = Intent(this@BankPaymentService, MainActivity::class.java)
                            i.flags = FLAG_ACTIVITY_NEW_TASK
                            i.putExtra("TOTAL", total)
                            this@BankPaymentService.startActivity(i)
                            Log.d("server", "initializado startActivity")
                            initialize = true
                        }
                    }
                    Log.d("server", "initializado sucesso finalizado")
                    resultAction
                } else {
                    Log.d("server", "initializado erro")
                    -1
                }
            }
        }
    }

    override fun onWaning(code: Int) {
        result = code
        running = false
        Log.d("server", "onWaning")
    }
}