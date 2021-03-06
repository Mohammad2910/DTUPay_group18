package domain;

import java.security.SecureRandom;

/**
 * class that generate a token
 * @author David
 */
public class TokenGenerator {

    private SecureRandom RNG = new SecureRandom();

    /**
     * Method that generates a single token of 32 bytes from a RNG (Random Number Generator)
     * @return token as a string
     */
    public String generate(){
        byte bytes[] = new byte[32];
        RNG.nextBytes(bytes);
        return bytes.toString();
    }
}
