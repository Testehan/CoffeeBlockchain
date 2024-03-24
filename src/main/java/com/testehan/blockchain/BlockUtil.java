package com.testehan.blockchain;

public class BlockUtil {

    public static String calculateHash(final Block block) {
        return StringUtil.applySha256(block.getHashableData());
    }
}
