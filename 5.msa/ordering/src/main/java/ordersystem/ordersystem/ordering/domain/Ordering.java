package ordersystem.ordersystem.ordering.domain;

import jakarta.persistence.*;
import lombok.*;
import ordersystem.ordersystem.common.domain.BaseTimeEntity;
import ordersystem.ordersystem.ordering.dto.OrderDetailResDto;
import ordersystem.ordersystem.ordering.dto.OrderListResDto;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Ordering extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memberEmail;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus= OrderStatus.ORDERED;

    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST) //remove는 사용하지 않는 이유는 주문을 취소시 삭제가 아니라 이넘타입을 CANCELLED로 할 꺼니까
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public OrderListResDto fromEntity(){
        List<OrderDetailResDto> orderDetailResDtos = new ArrayList<>();
        for(OrderDetail od : this.getOrderDetails()){
            OrderDetailResDto orderDetailResDto = OrderDetailResDto.builder()
                    .detailId(od.getId())
                    .count(od.getQuantity())
                    .build();
            orderDetailResDtos.add(orderDetailResDto);
        }
        OrderListResDto orderDto =  OrderListResDto.builder()
                .id(this.getId())
                .memberEmail(this.memberEmail)
                .orderStatus(this.getOrderStatus().toString())
                .orderDetails(orderDetailResDtos)
                .build();
        return orderDto;
    }

    public void cancelStatus(){
        this.orderStatus = OrderStatus.CANCELED;
    }
}

