package tokens;

import domain.TokenGenerator;
import domain.model.TokenSet;
import org.junit.jupiter.api.Test;
import storage.TokenStorage;

import static org.junit.jupiter.api.Assertions.*;

class TokenStorageTest {


    TokenGenerator generator = new TokenGenerator();

    private void setup(TokenSet tokenSet){

        for(int i = 0; i < 6; i++ ){
            tokenSet.addToken(generator.generate());
        }

    }

    @Test
    void addNewEntry() {
        TokenSet tokenSet = new TokenSet();
        TokenStorage storage = new TokenStorage();
        setup(tokenSet);
        String cid = "cid1";
        storage.addNewEntryToStorage(cid, tokenSet);
        assertNotNull(storage.getAllTokens().get("cid1"));
    }

    @Test
    void addTokens() {
        TokenSet tokenSet1 = new TokenSet();
        TokenSet tokenSet2 = new TokenSet();

        TokenStorage storage = new TokenStorage();
        String cid = "cid1";

        tokenSet1.addToken(generator.generate());
        storage.addNewEntryToStorage(cid, tokenSet1);

        //new tokens to be added
        tokenSet2.addToken(generator.generate());
        tokenSet2.addToken(generator.generate());

        //test to see the return tokenSet has added up the new tokenSet with the already existing tokenSet
        assertEquals(3, storage.addTokensToCustomer(cid, tokenSet2).findNumberOfTokens());

        //test to see if the additional tokens to the already existing tokenSet
        assertEquals(3, storage.getCustomerTokenSetSize(cid));
    }

    @Test
    void removeTokenFromCustomer() {

        TokenSet tokenSet = new TokenSet();
        TokenStorage storage = new TokenStorage();
        String cid = "cid1";
        String token = "token1";
        tokenSet.addToken(token);
        storage.addNewEntryToStorage(cid, tokenSet);
        storage.removeTokenFromCustomer(cid, token);
        assertFalse(storage.isCustomerTokenValid(cid, token));

    }

    @Test
    void getCustomerTokenSetSize() {

        TokenSet tokenSet = new TokenSet();
        TokenStorage storage = new TokenStorage();
        String cid = "cid1";

        tokenSet.addToken(generator.generate());
        storage.addNewEntryToStorage(cid, tokenSet);

        assertEquals(1, storage.getCustomerTokenSetSize(cid));

    }

    @Test
    void isCustomerTokenValid() {

        TokenSet tokenSet = new TokenSet();
        TokenStorage storage = new TokenStorage();
        String cid1 = "cid1";
        String token = generator.generate();

        tokenSet.addToken(generator.generate());
        tokenSet.addToken(token);
        tokenSet.addToken(generator.generate());
        storage.addNewEntryToStorage(cid1, tokenSet);

        assertTrue(storage.isCustomerTokenValid(cid1, token));


    }

    @Test
    void isCustomerCreated(){

        TokenSet tokenSet = new TokenSet();
        TokenStorage storage = new TokenStorage();
        String cid1 = "cid1";
        storage.addNewEntryToStorage(cid1, tokenSet);

        assertTrue(storage.isCustomerCreated(cid1));

    }

    @Test
    void getTokenHashMap(){
        TokenStorage storage = new TokenStorage();
        assertNotNull(storage.getAllTokens());
    }
}