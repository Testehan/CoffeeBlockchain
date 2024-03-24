package com.testehan.blockchain;

import java.util.List;

public class BlockchainUtil {

    public static Boolean isBlockchainValid(List<Block> blockchain) {
        Block currentBlock;
        Block previousBlock;

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.getCurrentBlockHash().equals(BlockUtil.calculateHash(currentBlock)) ){
                System.out.println("Current Hashes not equal");
                return false;
            }

            //compare previous hash and registered previous hash
            if(!previousBlock.getCurrentBlockHash().equals(currentBlock.getPreviousBlockHash()) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }

}
