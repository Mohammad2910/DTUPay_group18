package storage;

import domain.model.TokenSet;
import exceptions.TokensNotEnoughException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class that represents the storage unit of customer-tokenSet mappings
 *
 * @author Renjue, Christian and David
 */
public class TokenStorage implements ITokenStorage {

    HashMap<String, TokenSet> tokenHashMap = new HashMap<>();

    @Override
    public void addNewEntryToStorage(String cid, TokenSet tokens) {
        tokenHashMap.put(cid, tokens);
    }

    @Override
    public TokenSet addTokensToCustomer(String cid, TokenSet tokens) {
        TokenSet tokenSetFromStorage = tokenHashMap.get(cid);
        for (String token : tokens.getTokenSet()) {
            if (token != null) {
                tokenSetFromStorage.addToken(token);
            }
        }
        return tokenSetFromStorage;
    }

    @Override
    public void removeTokenFromCustomer(String cid, String token) {
        TokenSet set = tokenHashMap.get(cid);
        set.removeToken(token);
    }

    @Override
    public int getCustomerTokenSetSize(String cid) {
        TokenSet tokenSet = tokenHashMap.get(cid);
        return tokenSet.findNumberOfTokens();
    }

    //todo: throw an exception here if customer token is not valid
    @Override
    public boolean isCustomerTokenValid(String cid, String token) {
        TokenSet set = tokenHashMap.get(cid);
        return set.findToken(token);
    }

    @Override
    public String getCustomerByToken(String token) {
        for (Map.Entry<String, TokenSet> entry : tokenHashMap.entrySet()) {
            for (String item : entry.getValue().getTokenSet()) {
                if (token.equals(item)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }


    @Override
    public boolean isCustomerCreated(String cid) {
        return tokenHashMap.get(cid) != null;
    }

    @Override
    public String getToken(String cid){
        TokenSet tokenSet = tokenHashMap.get(cid);
        for (String item : tokenSet.getTokenSet()) {
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Override
    public HashMap<String, TokenSet> getAllTokens() {
        return tokenHashMap;
    }
}
