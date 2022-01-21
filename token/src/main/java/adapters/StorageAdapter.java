package adapters;

import domain.model.TokenSet;
import domain.ports.IStorageAdapter;
import storage.TokenStorage;

/**
 *
 * @author David
 */
public class StorageAdapter implements IStorageAdapter {

    private TokenStorage tokenStorage;

    public StorageAdapter(TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
    }

    @Override
    public int getNumberOfTokens(String cid) {
        return tokenStorage.getCustomerTokenSetSize(cid);
    }

    @Override
    public boolean hasToken(String cid, String token) {
        return tokenStorage.isCustomerTokenValid(cid, token);
    }

    @Override
    public String getCustomerByToken(String token) {
        return tokenStorage.getCustomerByToken(token);
    }

    @Override
    public boolean isCustomerCreatedInStorage(String cid) {
        return tokenStorage.isCustomerCreated(cid);
    }

    @Override
    public void storeToken(String cid, TokenSet tokens){
        tokenStorage.addTokensToCustomer(cid, tokens);
    }

    @Override
    public void consumeToken(String cid, String token) {
        tokenStorage.removeTokenFromCustomer(cid, token);
    }

    @Override
    public void addNewCustomer(String cid, TokenSet tokens) {
        tokenStorage.addNewEntryToStorage(cid, tokens);
    }

    @Override
    public String[] getTokens(String cid) {
        return tokenStorage.getTokens(cid);
    }
}
