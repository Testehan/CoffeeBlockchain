package com.testehan.blockchain.transaction;

import com.testehan.blockchain.CoffeeBlockchain;
import com.testehan.blockchain.StringUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private String transactionId; // this is also the hash of the transaction.
    private PublicKey sender; // senders address/public key.
    private PublicKey reciepient; // Recipients address/public key.
    private long value;  // this is in sathosi :)
    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

    private List<TransactionInput> inputTransactions;
    private List<TransactionOutput> outputTransactions = new ArrayList<>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, long value, List<TransactionInput> inputTransactions) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputTransactions = inputTransactions;
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                      StringUtil.getStringFromKey(reciepient) +
                      value + sequence
        );
    }

    // Signs all the data we don't wish to be tampered with.
    // In reality, you may want to sign more information, like the outputs/inputs used and/or
    // time-stamp ( for now we are just signing the bare minimum )
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + value;
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // Verifies the data we signed hasn't been tampered with
    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + value;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    //Returns true if new transaction could be created.
    public boolean processTransaction() {

        if (verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for (TransactionInput i : inputTransactions) {
            i.setUTXO(CoffeeBlockchain.unspentTransactions.get(i.getTransactionOutputId()));
        }

        //check if transaction is valid:
        if (getInputTransactionsTotal() < CoffeeBlockchain.minimumTransactionInSatoshi) {
            System.out.println("#Transaction Inputs to small: " + getInputTransactionsTotal());
            return false;
        }

        //generate transaction outputs:
        long leftOver = getInputTransactionsTotal() - value; //get value of inputs then the left over change:
        transactionId = calculateHash();
        outputTransactions.add(new TransactionOutput(this.reciepient, value, transactionId)); //send value to recipient
        outputTransactions.add(new TransactionOutput(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for (TransactionOutput o : outputTransactions) {
            CoffeeBlockchain.unspentTransactions.put(o.getId(), o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputTransactions) {
            if (i.getUTXO() == null) continue; //if Transaction can't be found skip it
            CoffeeBlockchain.unspentTransactions.remove(i.getUTXO().getId());
        }

        return true;
    }

    public long getInputTransactionsTotal() {
        long total = 0;
        for (TransactionInput i : inputTransactions) {
            if (i.getUTXO() == null) {
                continue; //if Transaction can't be found skip it
            }
            total = total + i.getUTXO().getValue();
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

    public PublicKey getReciepient() {
        return reciepient;
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
