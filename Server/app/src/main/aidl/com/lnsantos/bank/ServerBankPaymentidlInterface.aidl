// ServerBankPaymentidlInterface.aidl
package com.lnsantos.bank;

// Declare any non-default types here with import statements

interface ServerBankPaymentidlInterface {

    int send(long price, String clientRequest, String identification);
}