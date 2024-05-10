package kea.exam.template.product;

import kea.exam.template.category.Category;
import kea.exam.template.category.CategoryRepository;
import kea.exam.template.exceptions.BadRequestException;
import kea.exam.template.exceptions.EntityNotFoundException;
import kea.exam.template.product.dto.ProductRequestDTO;
import kea.exam.template.product.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductResponseDTO> getAllProducts(Integer pageNum, Integer pageSize, String sortDir, String sortBy) {

        Pageable pageable = PageRequest.of(
                pageNum,
                pageSize,
                Sort.Direction.valueOf(sortDir),
                sortBy
        );

        return productRepository.findAll(pageable).map(this::toDTO);





        /*return productRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();*/
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        boolean existsInDb = productRepository.existsByNameContainingIgnoreCase(productRequestDTO.name());

        //THOW HER
        if (existsInDb)
            throw new BadRequestException("Product with name " + productRequestDTO.name() + " already exists");


        // SKAL IKKE KOMME HERNED
        Product product = toEntity(productRequestDTO);
        return toDTO(productRepository.save(product));
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(productRequestDTO.name(), id));

        Category category = categoryRepository.findFirstByNameContainsIgnoreCase(productRequestDTO.category())
                .orElseThrow(() -> new EntityNotFoundException("Category", productRequestDTO.category()));

        productToUpdate.setName(productRequestDTO.name());
        productToUpdate.setImageURL(productRequestDTO.image());
        productToUpdate.setCategory(category);
        productToUpdate.setPrice(productRequestDTO.price());
        productToUpdate.setStock(productRequestDTO.stock());

        return toDTO(productRepository.save(productToUpdate));
    }

    public ProductResponseDTO toDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getImageURL(),
                product.getPrice(),
                product.getStock(),
                product.getCategory()
                        .getName()
        );
    }

    private Product toEntity(ProductRequestDTO productRequestDTO) {
        Category categoryInDB = categoryRepository.findFirstByNameContainsIgnoreCase(productRequestDTO.category())
                .orElseThrow(() -> new EntityNotFoundException("Category", productRequestDTO.category()));

        return new Product(
                productRequestDTO.name(),
                productRequestDTO.image(),
                productRequestDTO.price(),
                productRequestDTO.stock(),
                categoryInDB
        );
    }


}
