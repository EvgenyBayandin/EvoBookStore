package ru.fsdstudio.product.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fsdstudio.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}