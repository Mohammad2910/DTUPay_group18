package domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Christian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenPayload {
    String cid;
    String token;
    String[] tokens;
    int tokenAmount;
}
