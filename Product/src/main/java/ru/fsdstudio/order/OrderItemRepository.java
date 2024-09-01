package ru.fsdstudio.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id = :orderId")
    List<OrderItem> getItemsInOrder(@Param("orderId") Long orderId);
    
    @Query("SELECT SUM(oi.quantity) AS totalQuantity, SUM(oi.amount) AS totalPrice FROM OrderItem oi WHERE oi.order.id = :orderId")
    Object[] getTotalQuantityAndPrice(@Param("orderId") Long orderId);
}
