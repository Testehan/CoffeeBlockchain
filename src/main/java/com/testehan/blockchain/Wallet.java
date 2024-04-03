package com.testehan.blockchain;

import com.testehan.blockchain.transaction.Transaction;
import com.testehan.blockchain.transaction.TransactionInput;
import com.testehan.blockchain.transaction.TransactionOutput;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    // The private key is used to sign the data we don’t want to be tampered with.
    // The public key is used to verify the signature.
    private PrivateKey privateKey;       // used to sign our transactions, so that only the owner of a wallet can send coins from it
    private PublicKey publicKey;         // wallet address

    public Map<String,TransactionOutput> unspentTransactions = new HashMap<>();

    public Wallet(){
        generateKeyPair();
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();

            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void addUnspentTransaction(TransactionOutput unspentTransaction){
        if (unspentTransaction.belongsToWallet(publicKey)) {
            unspentTransactions.put(unspentTransaction.getId(), unspentTransaction);
        }
    }

    //Generates and returns a new transaction from this wallet.
    public Transaction sendFunds(PublicKey recipient, long value ) {
        //create array list of inputs
        List<TransactionInput> inputs = new ArrayList<>();

        long total = 0;
        for (Map.Entry<String, TransactionOutput> item: unspentTransactions.entrySet()){
            TransactionOutput unspentTransaction = item.getValue();
            total = total + unspentTransaction.getValue();
            inputs.add(new TransactionInput(unspentTransaction.getId()));
            if(total > value) {
                break;
            }
        }

        Transaction newTransaction = new Transaction(this, recipient , value, inputs);

        for(TransactionInput input: inputs){
            unspentTransactions.remove(input.getTransactionOutputId());
        }
        return newTransaction;
    }
}
