package com.testehan;

import com.google.gson.GsonBuilder;
import com.testehan.blockchain.Block;
import com.testehan.blockchain.BlockchainUtil;

import java.util.ArrayList;
import java.util.List;


public class CoffeeBlockchainMain
{
    public static List<Block> blockchain = new ArrayList<>();
    public static int difficulty = 7;   // this should be adjustable

    public static void main( String[] args )
    {

        blockchain.add(new Block("Hi im the first block", "0"));
        System.out.println("Trying to Mine block 1... ");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Yo im the second block",blockchain.get(blockchain.size()-1).currentBlockHash));
        System.out.println("Trying to Mine block 2... ");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Hey im the third block",blockchain.get(blockchain.size()-1).currentBlockHash));
        System.out.println("Trying to Mine block 3... ");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is Valid: " + BlockchainUtil.isBlockchainValid(blockchain));

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);

        System.out.println(BlockchainUtil.isBlockchainValid(blockchain));

        //screw up blockchain
        blockchain.add(new Block("Hey im the third block", "wrong previous hash"));
        System.out.println(BlockchainUtil.isBlockchainValid(blockchain));   // now this should return false
    }
}
