package ru.fsdstudio.product.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fsdstudio.product.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByProductId(Long productId);
}