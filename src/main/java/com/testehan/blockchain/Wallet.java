package com.testehan.blockchain;

import com.testehan.blockchain.transaction.Transaction;
import com.testehan.blockchain.transaction.TransactionInput;
import com.testehan.blockchain.transaction.TransactionOutput;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
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

    public void generateKeyPair() {
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

    //returns balance and stores the UTXO's owned by this wallet in this.UTXOs
    public long getBalance() {
        long total = 0;
        for (Map.Entry<String, TransactionOutput> item: CoffeeBlockchain.unspentTransactions.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                unspentTransactions.put(UTXO.getId(), UTXO); //add it to our list of unspent transactions.
                total += UTXO.getValue() ;
            }
        }
        return total;
    }
    //Generates and returns a new transaction from this wallet.
    public Transaction sendFunds(PublicKey recipient, long value ) {
        if(getBalance() < value) { //gather balance and check funds.
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        //create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        long total = 0;
        for (Map.Entry<String, TransactionOutput> item: unspentTransactions.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.getValue();
            inputs.add(new TransactionInput(UTXO.getId()));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient , value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            unspentTransactions.remove(input.getTransactionOutputId());
        }
        return newTransaction;
    }
}
