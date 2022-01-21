package domain.ports;

import domain.model.TokenSet;

/**
 * Interface that describes what methods that should be implemented by an adapter
 * @author David
 */
public interface IStorageAdapter {

    /**
     * Method for checking the amount of tokens in a tokenSet belonging to the specified customer
     *
     * @param cid - the id of the customer
     * @return Integer representing the amount of tokens in the tokenSet
     */
    int getNumberOfTokens(String cid);

    /**
     * Method for checking whether a there exists a specified token belonging to a specified customer
     *
     * @param cid - the id of the customer
     * @param token to check for
     * @return boolean which states whether the token was found
     */
    boolean hasToken(String cid, String token);

    /**
     * Method for getting the customer by a token
     * @param token - the token we want to validate a customer from
     * @return the customer id of the token owner
     */
    String getCustomerByToken(String token);

    /**
     * Method for checking if customer is created in storage
     *
     * @param cid - the id of the customer
     * @return boolean stating if the customer exists
     */
    boolean isCustomerCreatedInStorage(String cid);

    /**
     * Method for storing a tokenSet
     *
     * @param tokens to store
     */
    void storeToken(String cid, TokenSet tokens);

    /**
     * Method for consuming (removing) a specifid token belonging to a specified customer
     *
     * @param cid - the id of the customer
     * @param token to remove
     */
    void consumeToken(String cid, String token);

    /**
     * Method for adding a new customer with a newly generated TokenSet in storage
     *
     * @param cid - the id of the customer
     * @param tokens to add
     */
    void addNewCustomer(String cid, TokenSet tokens);


    /**
     * Method for getting a list of tokens from a specified customer
     * @param cid of the specified customer
     * @return a list of tokens of the customer
     */
    String[] getTokens(String cid);
}
