package domain;

import domain.model.TokenSet;
import domain.ports.IStorageAdapter;
import exceptions.TokenNotValidException;
import exceptions.TokenOutOfBoundsException;
import exceptions.TokensEnoughException;

/**
 * Class for managing tokens
 *
 * @author Renjue, Christian and David
 */
public class TokenManager {

    private TokenGenerator generator = new TokenGenerator();
    private IStorageAdapter storageAdapter;

    public TokenManager(IStorageAdapter storageAdapter) {
        this.storageAdapter = storageAdapter;
    }

    /**
     * Method for adding a new customer with a specified tokenSet
     * @param cid - the id of the customer
     * @param tokens that the customer possess
     */
    public void addNewCustomer(String cid, TokenSet tokens) {
        storageAdapter.addNewCustomer(cid, tokens);
    }

    /**
     * Method for checking if customer exists in storage
     * @param cid - the id of the customer
     * @return true if customer exists in storage, or false if not.
     */
    public boolean customerExistsInStorage(String cid){return storageAdapter.isCustomerCreatedInStorage(cid);}

    /**
     * Method for validating a specified customer with a specified token
     * @param cid - the id of the customer
     * @param token that the customer possess
     * @return boolean stating whether the possessed token is valid or not
     */
    public boolean validateToken(String cid, String token) throws TokenNotValidException {
        if (storageAdapter.hasToken(cid, token)){
            return storageAdapter.hasToken(cid, token);
        }
        throw new TokenNotValidException("Token is not valid!");
    }

    /**
     * Method for generating a tokenSet specified by amount without any checking
     * @param amount of tokens to be generated
     * @return a tokenSet specified by amount
     */
    public TokenSet generateTokens(int amount) {
        TokenSet set = new TokenSet();
        for (int i = 0; i < amount; i++) {
            set.addToken(generator.generate());
        }
        return set;
    }

    /**
     * Method for checking the amount of tokens that a specified customer has
     * @param cid - the id of the customer
     * @return the integer amount of tokens that the customer possess
     */
    public int checkCustomerTokenSetSize(String cid) {
        return storageAdapter.getNumberOfTokens(cid);
    }

    /**
     * Method for returning the updated tokenSet after a specified customer's request
     * @param cid - the id of the customer
     * @param amount that the customer request
     * @throws TokensEnoughException if the customer cannot request the specified amount
     */
    public void supplyTokens(String cid, int amount) throws TokensEnoughException, TokenOutOfBoundsException {
        if (checkCustomerTokenSetSize(cid) < 2) {  // the customer has 0 or 1 token
            if (checkCustomerTokenSetSize(cid) + amount < 7) {  // the maximal amount of tokens should not exceed 6
                storageAdapter.storeToken(cid, generateTokens(amount));
            } else {
                throw new TokenOutOfBoundsException("Customer requests too many tokens!");
            }
        } else {
            throw new TokensEnoughException("Customer has sufficient tokens and cannot request more!");
        }
    }

    /**
     * Method for storing a new TokenSet with specified amount of tokens in already existing storage
     * @param cid - the id of the customer
     * @param tokens that need to be stored in storage
     */
    public void storeTokens(String cid, TokenSet tokens) {
        storageAdapter.storeToken(cid, tokens);
    }

    /**
     * Method for consuming a token from customer
     * @param cid - the id of the customer
     * @param token that need to be consumed from storage
     */
    public void consumeToken(String cid, String token) {
        storageAdapter.consumeToken(cid, token);
    }

}
