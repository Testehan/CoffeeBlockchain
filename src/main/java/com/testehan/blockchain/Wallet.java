package com.testehan.blockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
    // The private key is used to sign the data we don’t want to be tampered with.
    // The public key is used to verify the signature.
    public PrivateKey privateKey;       // used to sign our transactions, so that only the owner of a wallet can send coins from it
    public PublicKey publicKey;         // wallet address

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
}
