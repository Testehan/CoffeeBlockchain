package com.testehan.blockchain.transaction;

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

    private List<TransactionInput> inputs = new ArrayList<>();
    private List<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, long value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
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
        signature = StringUtil.applyECDSASig(privateKey,data);
    }
    // Verifies the data we signed hasn't been tampered with
    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + value;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }
}
