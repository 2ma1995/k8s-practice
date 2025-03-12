package ordersystem.ordersystem.product.controller;

import ordersystem.ordersystem.product.domain.Product;
import ordersystem.ordersystem.product.dto.ProductRegisterDto;
import ordersystem.ordersystem.product.dto.ProductResDto;
import ordersystem.ordersystem.product.dto.ProductSearchDto;
import ordersystem.ordersystem.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<?> productCreate( ProductRegisterDto dto){
        Product product = productService.productCreate(dto);
        return new ResponseEntity<>(product.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> productList(Pageable pageable, ProductSearchDto dto){
        Page<ProductResDto> productResDtos = productService.findAll(pageable,dto);
        return new ResponseEntity<>(productResDtos,HttpStatus.OK) ;
    }

}
