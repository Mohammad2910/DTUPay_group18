package tokens;

import domain.TokenGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author Renjue and David
 */
class TokenGeneratorTest {

    TokenGenerator generator = new TokenGenerator();

    /**
     * Simple test to see that a token is generated. Since the token is psuedorandom we cannot
     * test for a specific output (just that it actually produces one)
     */
    @Test
    void generate() {
        assertNotNull(generator.generate());
    }
}