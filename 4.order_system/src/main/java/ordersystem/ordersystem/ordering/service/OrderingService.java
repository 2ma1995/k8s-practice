package ordersystem.ordersystem.ordering.service;

import jakarta.persistence.EntityNotFoundException;
import ordersystem.ordersystem.common.service.StockInventoryService;
import ordersystem.ordersystem.member.domain.Member;
import ordersystem.ordersystem.member.repository.MemberRepository;
import ordersystem.ordersystem.ordering.controller.SseController;
import ordersystem.ordersystem.ordering.domain.OrderDetail;
import ordersystem.ordersystem.ordering.domain.Ordering;
import ordersystem.ordersystem.ordering.dto.OrderCreateDto;
import ordersystem.ordersystem.ordering.dto.OrderDetailResDto;
import ordersystem.ordersystem.ordering.dto.OrderListResDto;
import ordersystem.ordersystem.ordering.repository.OrderingDetailRepository;
import ordersystem.ordersystem.ordering.repository.OrderingRepository;
import ordersystem.ordersystem.product.domain.Product;
import ordersystem.ordersystem.product.repository.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final OrderingDetailRepository orderingDetailRepository;
    private final ProductRepository productRepository;
    private final StockInventoryService stockInventoryService;
    private final SseController sseController;
    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, OrderingDetailRepository orderingDetailRepository, ProductRepository productRepository, StockInventoryService stockInventoryService, SseController sseController) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.orderingDetailRepository = orderingDetailRepository;
        this.productRepository = productRepository;
        this.stockInventoryService = stockInventoryService;
        this.sseController = sseController;
    }

    public Ordering orderCreate(List<OrderCreateDto> dtos) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Member not found"));
////        방법1. cacading 없이 db 저장
////        Ordering 객체 생성 및 save
//        Ordering ordering = Ordering.builder()
//                .member(member)
//                .build();
//        orderingRepository.save(ordering);
////        OrderingDetail 객체 생성 및 save
//        for (OrderCreateDto o : dtos) {
//            Product product = productRepository.findById(o.getProductId()).orElseThrow(()->new EntityNotFoundException("Product not found"));
//            int quantity  = o.getProductCount();
//            동시성 이슈를 고려안한 코드
//            if (product.getStockQuantity() < quantity){
//                throw new IllegalArgumentException("재고 부족");
//            } else {
//                // 재고 감소 로직
//              product.updateStockQuantity(o.getProductCount());
//            }
//            OrderDetail orderDetail = OrderDetail.builder()
//                    .ordering(ordering)
//                    .product(product)
//                    .quantity(o.getProductCount())
//                    .build();
//            orderingDetailRepository.save(orderDetail);
//        }
//        방법2. cacading 사용하여 db 저장
//        OrderingDetail 객체 생성하면서 OrderingDetail객체 같이 생성
        Ordering ordering = Ordering.builder()
                .member(member)
                .build();
        for (OrderCreateDto o : dtos) {
            Product product = productRepository.findById(o.getProductId()).orElseThrow(() -> new EntityNotFoundException("Product not found"));
            int quantity = o.getProductCount();
            //동시성 이슈를 고려안한 코드
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("재고 부족");
            } else {
                // 재고 감소 로직
                product.updateStockQuantity(o.getProductCount());
            }

//            //동시성 이슈를 고려한 코드
//            //redis를 통한 재고관리 및 재고잔량 확인
//            int newQuantity = stockInventoryService.decreaseStock(product.getId(), quantity);
//            if (newQuantity<0){
//                throw new IllegalArgumentException("재고 부족");
//            }
//            /*/
//            .productId(product.getId())
//                    .productCount(quantity)
//             */
//            //rdb동기화(rabbitmq)
//            StockRabbitDto stockRabbitDto = StockRabbitDto.builder()
//                    .productId(product.getId())
//                    .productCount(quantity)
//                    .build();
//            stockRabbitmqService.publish(stockRabbitDto);


            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(ordering)
                    .product(product)
                    .quantity(o.getProductCount())
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering ordering1=orderingRepository.save(ordering);

//        sse를통한 admin계정에 메시지 발송
        sseController.publishMessage(ordering1.fromEntity(),"admin@naver.com");
        return ordering;
    }

    public List<OrderListResDto> orderList(){
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for(Ordering o : orderings){
            List<OrderDetailResDto> orderDetailResDtos = new ArrayList<>();
            for(OrderDetail od : o.getOrderDetails()){
                OrderDetailResDto orderDetailResDto = OrderDetailResDto.builder()
                        .detailId(od.getId())
                        .productName(od.getProduct().getName())
                        .count(od.getQuantity())
                        .build();
                orderDetailResDtos.add(orderDetailResDto);
            }
            OrderListResDto orderDto = OrderListResDto
                    .builder()
                    .id(o.getId())
                    .memberEmail(o.getMember().getEmail())
                    .orderStatus(o.getOrderStatus().toString())
                    .orderDetails(orderDetailResDtos)
                    .build();
            orderListResDtos.add(orderDto);
        }
        return orderListResDtos;
    }

    public List<OrderListResDto> myOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("no member"));

        List<OrderListResDto> orderListResDtos = new ArrayList<>();

        for (Ordering o : member.getOrderingList()) {
//            List<OrderDetailResDto> orderDetailResDtos = new ArrayList<>();
//            for(OrderDetail od : o.getOrderDetails()){
//                OrderDetailResDto orderDetailResDto = OrderDetailResDto.builder()
//                        .productName(od.getProduct().getName())
//                        .detailId(od.getId())
//                        .count(od.getQuantity())
//                        .build();
//                orderDetailResDtos.add(orderDetailResDto);
//            }
//            OrderListResDto orderDto =  OrderListResDto.builder()
//                    .id(o.getId())
//                    .orderStatus(o.getOrderStatus().toString())
//                    .orderDetails(orderDetailResDtos)
//                    .memberEmail(o.getMember().getEmail())
//                    .build(); -->메서드화
            orderListResDtos.add(o.fromEntity());
        }
        return orderListResDtos;
    }

    public Ordering orderCancel(Long id){
        Ordering ordering = orderingRepository.findById(id).orElseThrow(()->new EntityNotFoundException("order is not found"));
        ordering.changeStatus();
//        orderingRepository.save(ordering);
        List<OrderDetail> orderDetailList = ordering.getOrderDetails();
        for(OrderDetail od : orderDetailList){
            Product product = od.getProduct();
            product.retrieveStrock(od.getQuantity());
        }
        return ordering;
    }

}
