package com.testehan.blockchain;

import com.testehan.blockchain.transaction.Transaction;
import com.testehan.blockchain.transaction.TransactionInput;
import com.testehan.blockchain.transaction.TransactionOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CoffeeBlockchain
{
    public static List<Block> blockchain = new ArrayList<>();
    public static int difficulty = 5;   // this should be adjustable
    public static long minimumTransactionInSatoshi = 5; // this should be adjustable

    public static Transaction genesisTransaction;

    public static Map<String, TransactionOutput> unspentTransactions = new HashMap<>();

    public static void addToBlockchain(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    // TODO Refactor this method.. it is too long and complex
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.getOutputTransactions().get(0).getId(), genesisTransaction.getOutputTransactions().get(0));

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.getCurrentBlockHash().equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getCurrentBlockHash().equals(currentBlock.getPreviousBlockHash()) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getCurrentBlockHash().substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifiySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputTransactionsTotal() != currentTransaction.getOutputTransactionsTotal()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.getInputTransactions()) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.getUnspentTransaction().getValue() != tempOutput.getValue()) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for(TransactionOutput output: currentTransaction.getOutputTransactions()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if( currentTransaction.getOutputTransactions().get(0).getRecipient() != currentTransaction.getRecipient()) {
                    System.out.println("#Transaction(" + t + ") output recipient is not who it should be");
                    return false;
                }
                if( currentTransaction.getOutputTransactions().get(1).getRecipient() != currentTransaction.getSender()) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static long getBalanceAvailableForWallet(Wallet wallet) {
        long balanceAvailable = 0;
        for (Map.Entry<String, TransactionOutput> item: unspentTransactions.entrySet()){
            TransactionOutput unspentTransaction = item.getValue();
            if (unspentTransaction.belongsToWallet(wallet.getPublicKey())) {
                wallet.addUnspentTransaction(unspentTransaction);
                balanceAvailable = balanceAvailable + unspentTransaction.getValue() ;
            }
        }
        return balanceAvailable;
    }

    public static boolean isTransactionValid(Transaction transaction) {

        if (transaction.verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for (TransactionInput i : transaction.getInputTransactions()) {
            i.setUnspentTransaction(unspentTransactions.get(i.getTransactionOutputId()));
        }

        //check if transaction is valid:
        long inputTransactionsTotal = transaction.getInputTransactionsTotal();
        if (inputTransactionsTotal < minimumTransactionInSatoshi) {
            System.out.println("#Transaction Inputs to small: " + inputTransactionsTotal);
            return false;
        }

        //generate transaction outputs:
        long leftOver = inputTransactionsTotal - transaction.getValue(); //get value of inputs then the left over change:
        // todo maybe shorthen these lines
        transaction.getOutputTransactions().add(new TransactionOutput(transaction.getRecipient(), transaction.getValue(), transaction.getTransactionId())); //send value to recipient
        transaction.getOutputTransactions().add(new TransactionOutput(transaction.getSender(), leftOver, transaction.getTransactionId())); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for (TransactionOutput o :  transaction.getOutputTransactions()) {
            unspentTransactions.put(o.getId(), o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i :  transaction.getInputTransactions()) {
            if (i.getUnspentTransaction() == null) {
                continue; //if Transaction can't be found skip it
            }
            unspentTransactions.remove(i.getUnspentTransaction().getId());
        }

        return true;
    }
}
