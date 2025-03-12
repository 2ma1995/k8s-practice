package ordersystem.ordersystem.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ordersystem.ordersystem.member.domain.Member;
import ordersystem.ordersystem.product.domain.Product;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class ProductRegisterDto {
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private MultipartFile productImage;

    public Product toEntity(Member member) {
        return Product.builder()
                .name(this.name)
                .category(this.category)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .member(member)
                .build();
    }
}
