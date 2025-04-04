package ai.nory.api.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class StaffMemberBinding {
    @CsvBindByName(column = "staff_id")
    private Long staffMemberId;

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "dob")
    private String dateOfBirth;

    @CsvBindByName(column = "role")
    private String role;

    @CsvBindByName(column = "iban")
    private String iban;

    @CsvBindByName(column = "bic")
    private String bic;

    @CsvBindByName(column = "location_id")
    private Long locationId;
}
