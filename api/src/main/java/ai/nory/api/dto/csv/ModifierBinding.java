package ai.nory.api.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ModifierBinding {
    @CsvBindByName(column = "modifier_id")
    private Long modifierTypeId;

    @CsvBindByName(column = "name")
    private String modifierTypeName;

    @CsvBindByName(column = "option")
    private String option;

    @CsvBindByName(column = "price")
    private BigDecimal price;
}
