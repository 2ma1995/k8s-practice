package ordersystem.ordersystem.ordering.repository;

import ordersystem.ordersystem.member.domain.Member;
import ordersystem.ordersystem.ordering.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderingRepository extends JpaRepository<Ordering,Long> {
    List<Ordering> findByMember(Member member);
}
