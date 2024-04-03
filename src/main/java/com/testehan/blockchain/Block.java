package com.testehan.blockchain;


import com.testehan.blockchain.transaction.Transaction;

import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String currentBlockHash;
    public String previousBlockHash;
    private long timeStamp; //millis since 1970.

    /*
        Bitcoin block 813,958, mined on Oct. 26, 2023 by AntPool, a cluster of miners known as a
        mining pool, had a nonce of 105,983,939.
        That day, AntPool had a bitcoin hashrate of 123 exa-hashes per second (or 123 quintillion
        hashes) and the entire Bitcoin network hashed at 428.22 exa-hashes per second
        (or 428.22 quintillion hashes). That means the AntPool mining pool alone generated
        quadrillions of numbers to find the correct nonce and winning hash, out of an exponentially
        many more number of trials for all mining pools combined
     */
    private int nonce; // https://en.wikipedia.org/wiki/Cryptographic_nonce

    private String merkleRoot;

    public ArrayList<Transaction> transactions = new ArrayList<>(); //our data will be a simple message.

    public Block(String previousHash ) {
        this.previousBlockHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.currentBlockHash = calculateHash();
    }

    public String getCurrentBlockHash() {
        return currentBlockHash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    // We will calculate the hash from all parts of the block we don’t want to be tampered with
    public String getHashableData(){
        return previousBlockHash + timeStamp + nonce + merkleRoot;
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);

        // target is a string containing a number of "difficulty" 0s    ..like "0000" for difficulty 4
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!currentBlockHash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            currentBlockHash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + currentBlockHash);
    }

    //Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null){
            return false;
        }

        if((!"0".equals(previousBlockHash))) {
            if((transaction.processTransaction() != true)) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    public String calculateHash() {
        return StringUtil.applySha256(getHashableData());
    }
}
