package tokens;

import adapters.StorageAdapter;
import domain.TokenBusinessLogic;
import domain.TokenGenerator;
import domain.model.TokenSet;
import domain.ports.IStorageAdapter;
import exceptions.*;
import org.junit.jupiter.api.Test;
import storage.TokenStorage;

import static org.junit.jupiter.api.Assertions.*;

class TokenBusinessLogicTest {

    TokenGenerator tokenGenerator = new TokenGenerator();
    TokenStorage TokenStorage = new TokenStorage();
    IStorageAdapter iStorageAdapter = new StorageAdapter(TokenStorage);
    TokenBusinessLogic tokenBusinessLogic = new TokenBusinessLogic(iStorageAdapter);

    @Test
    void createNewCustomer_Success() {
        String cid = "cid1";
        try {
            tokenBusinessLogic.createNewCustomer(cid);
            assertTrue(tokenBusinessLogic.customerExistsInStorage(cid));
        }catch (CustomerAlreadyExistsException customerAlreadyExistsException){
            assertThrows(CustomerAlreadyExistsException.class, () -> tokenBusinessLogic.createNewCustomer(cid));
        }
    }

    @Test
    void createNewCustomer_ThrowsCustomerAlreadyExistsException() {
        String cid = "cid1";
        try {
            tokenBusinessLogic.createNewCustomer(cid);
            tokenBusinessLogic.createNewCustomer(cid);
        }catch (CustomerAlreadyExistsException customerAlreadyExistsException){
            assertThrows(CustomerAlreadyExistsException.class, () -> tokenBusinessLogic.createNewCustomer(cid));
        }
    }

    @Test
    void addNewCustomer() {
        String cid = "cid1";
        TokenSet set = new TokenSet();
        tokenBusinessLogic.addNewCustomer(cid, set);

        //customer is stored
        assertTrue(tokenBusinessLogic.customerExistsInStorage(cid));
        //customer is not stored
        assertFalse(tokenBusinessLogic.customerExistsInStorage("cid2"));
    }

    @Test
    void validateToken() {
        String cid = "cid1";
        String token1 = "token1";
        String token2 = "token2";
        String token3 = "token3";
        TokenSet set = new TokenSet();
        set.addToken(token1);
        set.addToken(token2);
        set.addToken(token3);
        try {
            tokenBusinessLogic.addNewCustomer(cid, set);
            assertTrue(tokenBusinessLogic.validateToken(cid, token2));
        } catch (TokenNotValidException exception) {
            assertThrows(TokenNotValidException.class, () -> tokenBusinessLogic.validateToken(cid, "token4"));
        }
    }

    @Test
    void validateToken_ThrowsTokenNotValidException() {
        String cid = "cid1";
        try{
            tokenBusinessLogic.createNewCustomer(cid);
        }catch (CustomerAlreadyExistsException customerAlreadyExistsException){
           customerAlreadyExistsException.printStackTrace();
        }
        assertThrows(TokenNotValidException.class, () -> tokenBusinessLogic.validateToken(cid, "tokenNotAdded"));
    }

    @Test
    void generateToken() {
        int amount = 3;
        assertEquals(3, tokenBusinessLogic.generateTokens(amount).findNumberOfTokens());
    }

    @Test
    void checkCustomerTokenSetSize() {
        String cid = "cid1";
        TokenSet set = new TokenSet();
        set.addToken(tokenGenerator.generate());
        set.addToken(tokenGenerator.generate());
        set.addToken(tokenGenerator.generate());
        set.addToken(tokenGenerator.generate());
        tokenBusinessLogic.addNewCustomer(cid, set);
        assertEquals(4, tokenBusinessLogic.checkCustomerTokenSetSize(cid));
    }

    /*
     Case 1: Customer requests tokens when:
            - having 0 or 1 token left
            - requested amount of tokens is sufficient
     */
    @Test
    void supplyTokens_Success() {
        try {
            String cid1 = "cid1";
            TokenSet set1 = new TokenSet();
            set1.addToken(tokenGenerator.generate());
            tokenBusinessLogic.addNewCustomer(cid1, set1);
            tokenBusinessLogic.supplyTokens(cid1, 4);
            assertEquals(5, tokenBusinessLogic.checkCustomerTokenSetSize(cid1));

        } catch (TokenOutOfBoundsException | TokensEnoughException e) {
            e.printStackTrace();
        }
    }

    /*
     Case 2: Customer requests tokens when:
            - having 0 or 1 tokens left
            - requested amount of tokens exceeds max. limit of tokens.
     */
    @Test
    void supplyTokens_ThrowsTokenOutOfBoundsException() {
        String cid2 = "cid2";
        TokenSet set2 = new TokenSet();
        tokenBusinessLogic.addNewCustomer(cid2, set2);
        assertThrows(TokenOutOfBoundsException.class, () -> tokenBusinessLogic.supplyTokens(cid2, 7));
    }

    /*
     Case 3: Customer requests tokens when
            - having more than 1 token left.
     */
    @Test
    void supplyTokens_ThrowsTokensEnoughException() {
        String cid3 = "cid3";
        TokenSet set3 = new TokenSet();
        set3.addToken(tokenGenerator.generate());
        set3.addToken(tokenGenerator.generate());
        tokenBusinessLogic.addNewCustomer(cid3, set3);
        assertThrows(TokensEnoughException.class, () -> tokenBusinessLogic.supplyTokens(cid3, 1));
    }

    @Test
    void storeTokens() {
        String cid = "cid1";
        TokenSet set1 = new TokenSet();
        set1.addToken(tokenGenerator.generate());
        tokenBusinessLogic.addNewCustomer(cid, set1);

        TokenSet set2 = tokenBusinessLogic.generateTokens(3);

        tokenBusinessLogic.storeTokens(cid, set2);
        assertEquals(4, tokenBusinessLogic.checkCustomerTokenSetSize(cid));
    }

    @Test
    void consumeToken() {
        String cid = "cid1";
        TokenSet set = new TokenSet();
        String token1 = tokenGenerator.generate();
        String token2 = tokenGenerator.generate();
        set.addToken(token1);
        set.addToken(token2);
        tokenBusinessLogic.addNewCustomer(cid, set);
        assertEquals(2, tokenBusinessLogic.checkCustomerTokenSetSize(cid));
        tokenBusinessLogic.consumeToken(cid, token1);
        assertEquals(1, tokenBusinessLogic.checkCustomerTokenSetSize(cid));
    }

    @Test
    void validateCustomerFromToken_Success() throws TokenNotValidException {
        String cid = "cid1";
        TokenSet set = new TokenSet();
        String token1 = tokenGenerator.generate();
        String token2 = tokenGenerator.generate();
        set.addToken(token1);
        set.addToken(token2);
        tokenBusinessLogic.addNewCustomer(cid, set);
        assertEquals(cid, tokenBusinessLogic.validateCustomerFromToken(token1));
        assertEquals(cid, tokenBusinessLogic.validateCustomerFromToken(token2));
    }

    @Test
    void validateCustomerFromToken_ThrowsTokenNotValidException(){
        String cid = "cid1";
        TokenSet set = new TokenSet();
        String token1 = tokenGenerator.generate();
        String token2 = tokenGenerator.generate();
        set.addToken(token1);
        set.addToken(token2);
        tokenBusinessLogic.addNewCustomer(cid, set);
        assertThrows(TokenNotValidException.class, () -> tokenBusinessLogic.validateCustomerFromToken("token1"));

    }

    @Test
    void getTokens_Success() throws TokensNotEnoughException {
        String cid = "cid1";
        TokenSet set = new TokenSet();
        String token1 = tokenGenerator.generate();
        String token2 = tokenGenerator.generate();
        set.addToken(token1);
        set.addToken(token2);
        tokenBusinessLogic.addNewCustomer(cid, set);
        assertEquals(token1, tokenBusinessLogic.getTokens(cid)[0]);
        assertEquals(token2, tokenBusinessLogic.getTokens(cid)[1]);
        assertNull(tokenBusinessLogic.getTokens(cid)[2]);
        assertNull(tokenBusinessLogic.getTokens(cid)[3]);
        assertNull(tokenBusinessLogic.getTokens(cid)[4]);
        assertNull(tokenBusinessLogic.getTokens(cid)[5]);
    }

    @Test
    void getTokens_ThrowsTokenNotEnoughException() {
        String cid = "cid1";
        TokenSet set = new TokenSet();
        tokenBusinessLogic.addNewCustomer(cid, set);
        assertThrows(TokensNotEnoughException.class, () -> tokenBusinessLogic.getTokens(cid));
    }
}

