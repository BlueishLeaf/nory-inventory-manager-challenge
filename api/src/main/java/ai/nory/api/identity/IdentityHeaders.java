package ai.nory.api.identity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IdentityHeaders {
    private Long locationId;
    private Long staffMemberId;
}
