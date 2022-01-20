package facade.adapters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenPayload {
    String cid;
    String token;
    String[] tokens;
    int tokenAmount;
}
