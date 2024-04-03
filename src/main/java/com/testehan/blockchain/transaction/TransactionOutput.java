package com.testehan.blockchain.transaction;

import com.testehan.blockchain.StringUtil;

import java.security.PublicKey;

public class TransactionOutput {

    private String id;
    private PublicKey reciepient; //also known as the new owner of these coins.
    private long value; //the amount shatosi they own
    private String parentTransactionId; //the id of the transaction this output was created in

    //Constructor
    public TransactionOutput(PublicKey reciepient, long value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient) + value + parentTransactionId);
    }

    //Check if coin belongs to you
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }

    public long getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public PublicKey getReciepient() {
        return reciepient;
    }
}
