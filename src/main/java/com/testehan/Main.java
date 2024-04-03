package com.testehan;

import com.google.gson.GsonBuilder;
import com.testehan.blockchain.Block;
import com.testehan.blockchain.Wallet;
import com.testehan.blockchain.transaction.Transaction;
import com.testehan.blockchain.transaction.TransactionOutput;

import java.security.Security;

import static com.testehan.blockchain.CoffeeBlockchain.*;
import static com.testehan.blockchain.CoffeeBlockchain.isChainValid;

public class Main {

    public static void main(String[] args) {
        firstBlockchainTest();
//        test_wallets_and_a_single_transaction();
    }


    private static void test_wallets_and_a_single_transaction(){
        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Create the new wallets
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinbase = new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA:
        genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 2000000000, null);
        genesisTransaction.generateSignature(coinbase.getPrivateKey());	 //manually sign the genesis transaction
        genesisTransaction.setTransactionId("0"); //manually set the transaction id
        genesisTransaction.addOutputTransaction(new TransactionOutput(genesisTransaction.getReciepient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId())); //manually add the Transactions Output
        unspentTransactions.put(genesisTransaction.getOutputTransactions().get(0).getId(), genesisTransaction.getOutputTransactions().get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //testing
        Block block1 = new Block(genesis.getCurrentBlockHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getCurrentBlockHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getCurrentBlockHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();



//        //Test public and private keys
//        System.out.println("Private and public keys:");
//        System.out.println("Private key WalletA: " + StringUtil.getStringFromKey(walletA.getPrivateKey()));
//        System.out.println("Public key WalletA: " + StringUtil.getStringFromKey(walletA.getPublicKey()));
//        System.out.println("Private key WalletB: " + StringUtil.getStringFromKey(walletB.getPrivateKey()));
//        System.out.println("Public key WalletB: " + StringUtil.getStringFromKey(walletB.getPublicKey()));
//
//        //Create a test transaction from WalletA to walletB
//        Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
//        transaction.generateSignature(walletA.getPrivateKey());
//
//        //Verify the signature works and verify it from the public key
//        System.out.println("Is signature verified");
//        System.out.println(transaction.verifiySignature());
    }

    private static void firstBlockchainTest() {
        blockchain.add(new Block( "0"));
        System.out.println("Trying to Mine block 1... ");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block(blockchain.get(blockchain.size()-1).currentBlockHash));
        System.out.println("Trying to Mine block 2... ");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block(blockchain.get(blockchain.size()-1).currentBlockHash));
        System.out.println("Trying to Mine block 3... ");
        blockchain.get(2).mineBlock(difficulty);

//        System.out.println("\nBlockchain is Valid: " + isChainValid());

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);

//        System.out.println(isChainValid());

        //screw up blockchain
        blockchain.add(new Block( "wrong previous hash"));
//        System.out.println(isChainValid());   // now this should return false because we added an invalid hash in last block
    }
}
