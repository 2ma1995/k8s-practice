package ordersystem.ordersystem.ordering.domain;

import jakarta.persistence.*;
import lombok.*;
import ordersystem.ordersystem.common.domain.BaseTimeEntity;
import ordersystem.ordersystem.product.domain.Product;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class OrderDetail extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_id")
    private Ordering ordering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
