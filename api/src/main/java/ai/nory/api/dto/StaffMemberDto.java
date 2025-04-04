package ai.nory.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffMemberDto {
    private Long id;
    private String name;
    private Date dateOfBirth;
    private String role;
    private String iban;
    private String bic;
    private List<LocationDto> locations;
}
