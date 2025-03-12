package ordersystem.ordersystem.product.domain;

import jakarta.persistence.*;
import lombok.*;
import ordersystem.ordersystem.common.domain.BaseTimeEntity;
import ordersystem.ordersystem.member.domain.Member;
import ordersystem.ordersystem.product.dto.ProductResDto;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private Integer price;

    private Integer stockQuantity;

    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public ProductResDto fromEntity(){
        return ProductResDto.builder()
                .id(this.id).name(this.name).price(this.price).stockQuantity(this.stockQuantity)
                .category(this.category).imagePath(this.imagePath)
                .build();
    }

    public void updateImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public void updateStockQuantity(int stockQuantity){
        this.stockQuantity -= stockQuantity;
    }
    public void retrieveStrock(int a ){
        this.stockQuantity += a;
    }
}

