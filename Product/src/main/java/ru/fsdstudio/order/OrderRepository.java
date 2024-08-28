package ru.fsdstudio.order;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    default Page<Order> findByCustomerId(Pageable pageable, Long customerId) {
        List<Order> orders = findAll(pageable).stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
        return new PageImpl<>(orders, pageable, orders.size());
    }
}