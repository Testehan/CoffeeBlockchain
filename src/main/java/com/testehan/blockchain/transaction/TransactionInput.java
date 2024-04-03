package com.testehan.blockchain.transaction;

public class TransactionInput {

    private String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    private TransactionOutput unspentTransaction; // Contains the Unspent transaction output

    public TransactionInput(TransactionOutput unspentTransaction) {
        this.transactionOutputId = unspentTransaction.getId();
        this.unspentTransaction = unspentTransaction;
    }

    public String getTransactionOutputId() {
        return transactionOutputId;
    }

    public TransactionOutput getUnspentTransaction() {
        return unspentTransaction;
    }

    public void setUnspentTransaction(TransactionOutput unspentTransaction) {
        this.unspentTransaction = unspentTransaction;
    }
}
