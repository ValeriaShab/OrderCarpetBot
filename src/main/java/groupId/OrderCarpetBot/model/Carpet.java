package groupId.OrderCarpetBot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Carpet {
    private int id;

    private String name;

    private String size;

    private int price;

    private String photo;


}
