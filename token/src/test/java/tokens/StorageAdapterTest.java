package tokens;

import adapters.StorageAdapter;
import domain.TokenGenerator;
import domain.model.TokenSet;
import org.junit.jupiter.api.Test;
import storage.TokenStorage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Renjue, Christian and David
 */
class StorageAdapterTest {

    TokenGenerator generator = new TokenGenerator();
    TokenStorage TokenStorage = new TokenStorage();
    StorageAdapter storageAdapter = new StorageAdapter(TokenStorage);

    @Test
    void storageCheckCustomerTokenSize() {
        String cid = "cid1";
        TokenSet set = new TokenSet();
        set.addToken(generator.generate());
        storageAdapter.addNewCustomer(cid, set);
        assertEquals(1, storageAdapter.getNumberOfTokens(cid));
    }

    @Test
    void storageCheckCustomerToken() {
        String cid = "cid1";
        String token = "token1";
        TokenSet set = new TokenSet();
        set.addToken(token);
        storageAdapter.addNewCustomer(cid, set);
        assertTrue(storageAdapter.hasToken(cid,token));
    }

    @Test
    void storageStoreTokens() {
        //add one initial tokenSet with one single token
        String cid = "cid1";
        TokenSet set = new TokenSet();
        set.addToken(generator.generate());
        storageAdapter.addNewCustomer(cid, set);

        //now we add a new tokenset to the initial tokenSet
        TokenSet newSet = new TokenSet();
        newSet.addToken(generator.generate());
        newSet.addToken(generator.generate());
        storageAdapter.storeToken(cid, newSet);

        //now there should be a total of 3 tokens in the tokenSet
        assertEquals(3, storageAdapter.getNumberOfTokens(cid));
    }

    @Test
    void storageConsumeToken() {
        //Creates a customer with a tokenSet of 2 tokens
        String cid = "cid1";
        String token1 = "token1";
        String token2 = "token2";
        TokenSet set = new TokenSet();
        set.addToken(token1);
        set.addToken(token2);
        storageAdapter.addNewCustomer(cid, set);

        //now we consume token2 and checks that it was removed
        storageAdapter.consumeToken(cid, token2);
        assertFalse(storageAdapter.hasToken(cid, token2));

    }

    @Test
    void storageAddNewCustomer() {
        String cid = "cid1";
        TokenSet set = new TokenSet();
        set.addToken(generator.generate());
        storageAdapter.addNewCustomer(cid, set);
        //call method from IStorageAdapter that returns a map or cid?
        assertTrue(storageAdapter.isCustomerCreatedInStorage(cid));
    }
}