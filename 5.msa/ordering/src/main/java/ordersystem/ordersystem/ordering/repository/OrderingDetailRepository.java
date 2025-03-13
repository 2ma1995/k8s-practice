package ordersystem.ordersystem.ordering.repository;

import ordersystem.ordersystem.ordering.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderingDetailRepository extends JpaRepository<OrderDetail,Long> {
}
