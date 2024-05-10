package kea.exam.template.product;

import jakarta.validation.Valid;
import kea.exam.template.product.dto.ProductRequestDTO;
import kea.exam.template.product.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {


    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0", required = false) Integer pageNum,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(productService.getAllProducts(pageNum, pageSize));
    }


    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProduct.id())
                .toUri();

        return ResponseEntity.created(location).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProductById(@PathVariable("id") Long id, @RequestBody ProductRequestDTO productRequestDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequestDTO));
    }
}
