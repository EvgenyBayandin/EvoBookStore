package ru.fsdstudio.product.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fsdstudio.product.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {}