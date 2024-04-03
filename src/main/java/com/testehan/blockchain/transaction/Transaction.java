package com.testehan.blockchain.transaction;

import com.testehan.blockchain.Wallet;
import com.testehan.blockchain.util.StringUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private String transactionId; // this is also the hash of the transaction.
    private PublicKey sender; // senders address/public key.
    private PublicKey recipient; // Recipients address/public key.
    private long value;  // this is in sathosi :)
    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

    private List<TransactionInput> inputTransactions = new ArrayList<>();
    private List<TransactionOutput> outputTransactions = new ArrayList<>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    // Constructor:
    public Transaction(Wallet from, PublicKey to, long value, List<TransactionInput> inputTransactions) {
        this.sender = from.getPublicKey();
        this.recipient = to;
        this.value = value;
        if (inputTransactions != null) {
            this.inputTransactions = inputTransactions;
        }
        this.transactionId = calculateHash();

        generateSignature(from.getPrivateKey());
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                      StringUtil.getStringFromKey(recipient) +
                      value + sequence
        );
    }

    // Signs all the data we don't wish to be tampered with.
    // In reality, you may want to sign more information, like the outputs/inputs used and/or
    // time-stamp ( for now we are just signing the bare minimum )
    private void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // Verifies the data we signed hasn't been tampered with
    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public long getInputTransactionsTotal() {
        long total = 0;
        for (TransactionInput i : inputTransactions) {
            if (i.getUnspentTransaction() == null) {
                continue; //if Transaction can't be found skip it
            }
            total = total + i.getUnspentTransaction().getValue();
        }
        return total;
    }

    public long getOutputTransactionsTotal() {
        long total = 0;
        for (TransactionOutput o : outputTransactions) {
            total = total + o.getValue();
        }
        return total;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void addOutputTransaction(TransactionOutput transactionOutput) {
        this.outputTransactions.add(transactionOutput);
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public long getValue() {
        return value;
    }

    public List<TransactionOutput> getOutputTransactions() {
        return outputTransactions;
    }

    public List<TransactionInput> getInputTransactions() {
        return inputTransactions;
    }

    public PublicKey getSender() {
        return sender;
    }
}
