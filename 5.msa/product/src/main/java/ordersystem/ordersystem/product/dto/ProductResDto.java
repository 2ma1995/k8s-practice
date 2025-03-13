package ordersystem.ordersystem.product.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ordersystem.ordersystem.product.domain.Product;
import ordersystem.ordersystem.product.service.ProductService;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class ProductResDto {

    private Long id;
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private String imagePath;

}
