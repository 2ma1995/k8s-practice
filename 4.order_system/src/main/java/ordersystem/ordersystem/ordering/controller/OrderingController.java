package ordersystem.ordersystem.ordering.controller;

import ordersystem.ordersystem.ordering.domain.Ordering;
import ordersystem.ordersystem.ordering.dto.OrderCreateDto;
import ordersystem.ordersystem.ordering.dto.OrderListResDto;
import ordersystem.ordersystem.ordering.service.OrderingService;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordering")
public class OrderingController {
    private final OrderingService orderingService;

    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> orderCreate(@RequestBody List<OrderCreateDto> dtos) {
        Ordering ordering = orderingService.orderCreate(dtos);
        return new ResponseEntity<>(ordering.getId(), HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> orderList() {
        List<OrderListResDto> orderListDto = orderingService.orderList();
        return new ResponseEntity<>(orderListDto, HttpStatus.OK);
    }
    @GetMapping("/myorders")
    public ResponseEntity<?> myOrders(){
        List<OrderListResDto> dto = orderingService.myOrders();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> orderCancel(@PathVariable Long id){
        Ordering ordering = orderingService.orderCancel(id);
        return new ResponseEntity<>(ordering.getId(),HttpStatus.OK);
    }



}
