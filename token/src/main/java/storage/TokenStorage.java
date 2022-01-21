package storage;

import domain.model.TokenSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents the storage unit of customer-tokenSet mappings
 *
 * @author Renjue, Christian and David
 */
public class TokenStorage {

    private HashMap<String, TokenSet> tokenHashMap = new HashMap<>();

    /**
     * Adds a new customer-tokenSet mapping to the storage
     * @param cid - the id of the customer
     * @param tokens that the customer possess
     */
    public void addNewEntryToStorage(String cid, TokenSet tokens) {
        tokenHashMap.put(cid, tokens);
    }

    /**
     * Method for adding a new set of tokens to a customers existing tokenSet
     * @param cid - the id of the customer
     * @param tokens to add to the already existing tokenSet
     * @return a tokenSet upon completion of the adding behavior
     */
    public TokenSet addTokensToCustomer(String cid, TokenSet tokens) {
        TokenSet tokenSetFromStorage = tokenHashMap.get(cid);
        for (String token : tokens.getTokenSet()) {
            if (token != null) {
                tokenSetFromStorage.addToken(token);
            }
        }
        return tokenSetFromStorage;
    }

    /**
     * Method for removing a token from a specified customers tokenSet
     * @param cid - the id of the customer
     * @param token to be removed from the customer's tokenSet
     */
    public void removeTokenFromCustomer(String cid, String token) {
        TokenSet set = tokenHashMap.get(cid);
        set.removeToken(token);
    }

    /**
     * Method for getting the amount of tokens in a specified customer's tokenSet
     * @param cid - the id of the customer
     * @return Integer specifying the amount of tokens in the tokenSet
     */
    public int getCustomerTokenSetSize(String cid) {
        TokenSet tokenSet = tokenHashMap.get(cid);
        return tokenSet.findNumberOfTokens();
    }

    /**
     * Method for checking if a specified token exists in a specified customer's tokenSet
     * @param cid - the id of the customer
     * @param token to be checked
     * @return boolean stating whether the specified token did exist or not
     */
    public boolean isCustomerTokenValid(String cid, String token) {
        TokenSet set = tokenHashMap.get(cid);
        return set.findToken(token);
    }

    /**
     * Method for getting a customer from a given token
     * @param token - the token to get a customer from
     * @return - the customer id of the token owner
     */
    public String getCustomerByToken(String token) {
        for (Map.Entry<String, TokenSet> entry : tokenHashMap.entrySet()) {
            for (String item : entry.getValue().getTokenSet()) {
                if (token != null && token.equals(item)) {
                    System.out.println("============ token found ===================");
                    return entry.getKey();
                }
            }
        }
        return null;
    }


    /**
     * Method for checking if customer is created in storage
     * @param cid - the id of the customer
     * @return boolean stating if the customer exists
     */
    public boolean isCustomerCreated(String cid) {
        return tokenHashMap.get(cid) != null;
    }

    /**
     * Method for getting a token by a customerId
     * @param cid - the id of the customer
     * @return a list of tokens that the customer possess
     */
    public String[] getTokens(String cid){
        return tokenHashMap.get(cid).getTokenSet();
    }

    /**
     * Method for getting the whole hashmap (storage) of all customer-tokenSet mappings
     * @return Hashmap (storage)
     */
    public HashMap<String, TokenSet> getAllTokens() {
        return tokenHashMap;
    }
}

