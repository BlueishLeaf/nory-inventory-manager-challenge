package ai.nory.api.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class LocationBinding {
    @CsvBindByName(column = "location_id")
    private Long locationId;

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "address")
    private String address;
}
