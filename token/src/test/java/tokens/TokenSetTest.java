package tokens;

import domain.TokenGenerator;
import domain.model.TokenSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenSetTest {


    TokenGenerator generator = new TokenGenerator();
    TokenSet set;

    @Test
    void add() {
        set = new TokenSet();
        String token = generator.generate();
        set.addToken(token);
        assertEquals(token, set.getToken(0));
    }

    @Test
    void getToken(){
        set = new TokenSet();
        String token = generator.generate();
        set.addToken(token);
        assertNotNull(set.getToken(0));
    }

    @Test
    void remove(){
        set = new TokenSet();
        String token = generator.generate();
        set.addToken(token);
        set.removeToken(token);
        assertNull(set.getToken(0));

    }

    @Test
    void searchForToken(){
        String token = "str2";
        set = new TokenSet();
        set.addToken("str1");
        set.addToken(token);
        set.addToken("str3");
        assertTrue(set.findToken(token));
        assertFalse(set.findToken("str4"));
    }

    @Test
    void numberOfTokens(){
        set = new TokenSet();
        set.addToken("str1");
        set.addToken("str2");
        set.addToken("str3");
        assertEquals(3, set.findNumberOfTokens());
    }

    @Test
    void getTokenIndex(){
        set = new TokenSet();
        set.addToken("str1");
        set.addToken("str2");
        set.addToken("str3");
        assertEquals(2, set.getTokenIndex("str3"));
    }

    @Test
    void getSet(){
        set = new TokenSet();
        assertNotNull(set.getTokenSet());
    }
}