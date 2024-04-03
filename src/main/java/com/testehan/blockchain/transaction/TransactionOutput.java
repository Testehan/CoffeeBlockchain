package com.testehan.blockchain.transaction;

import com.testehan.blockchain.util.StringUtil;

import java.security.PublicKey;

public class TransactionOutput {

    private String id;
    private PublicKey recipient; //also known as the new owner of these coins.
    private long value; //the amount shatosi they own
    private String parentTransactionId; //the id of the transaction this output was created in

    //Constructor
    public TransactionOutput(PublicKey reciepient, long value, String parentTransactionId) {
        this.recipient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient) + value + parentTransactionId);
    }

    //Check if coin belongs to you
    public boolean belongsToWallet(PublicKey publicKey) {
        return (publicKey == recipient);
    }

    public long getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public PublicKey getRecipient() {
        return recipient;
    }
}
