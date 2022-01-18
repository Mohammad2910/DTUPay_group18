package adapters;

import domain.model.TokenSet;
import domain.ports.IStorageAdapter;
import storage.ITokenStorage;

public class StorageAdapter implements IStorageAdapter {

    private ITokenStorage tokenStorage;

    public StorageAdapter(ITokenStorage tokenStorage) {
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
}
