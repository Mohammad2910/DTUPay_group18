package storage;

import domain.model.TokenSet;
import exceptions.TokensNotEnoughException;

import java.util.HashMap;

/**
 * Interface for methods that should be supported by an external storage unit
 * @author Renjue, Christian and David
 */
public interface ITokenStorage {

    /**
     * Adds a new customer-tokenSet mapping to the storage
     * @param cid - the id of the customer
     * @param tokens that the customer possess
     */
    void addNewEntryToStorage(String cid, TokenSet tokens);

    /**
     * Method for adding a new set of tokens to a customers existing tokenSet
     * @param cid - the id of the customer
     * @param tokens to add to the already existing tokenSet
     */
    TokenSet addTokensToCustomer(String cid, TokenSet tokens);

    /**
     * Method for removing a token from a specified customers tokenSet
     * @param cid - the id of the customer
     * @param token to be removed from the customer's tokenSet
     */
    void removeTokenFromCustomer(String cid, String token);

    /**
     * Method for getting the amount of tokens in a specified customer's tokenSet
     * @param cid - the id of the customer
     * @return Integer specifying the amount of tokens in the tokenSet
     */
    int getCustomerTokenSetSize(String cid);

    /**
     * Method for checking if a specified token exists in a specified customer's tokenSet
     * @param cid - the id of the customer
     * @param token to be checked
     * @return boolean stating whether the specified token did exist or not
     */
    boolean isCustomerTokenValid(String cid, String token);

    /**
     * Method for getting a customer from a given token
     * @param token - the token to get a customer from
     * @return - the customer id of the token owner
     */
    String getCustomerByToken(String token);

    /**
     * Method for checking if customer is created in storage
     * @param cid - the id of the customer
     * @return boolean stating if the customer exists
     */
    boolean isCustomerCreated(String cid);


    String getToken(String cid);

    /**
     * Method for getting the whole hashmap (storage) of all customer-tokenSet mappings
     * @return Hashmap (storage)
     */
    HashMap<String, TokenSet> getAllTokens();
}
