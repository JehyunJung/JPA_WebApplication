package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager entityManager;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> orders = findOrders();
        orders.stream().forEach((o)->o.setOrderItems(findOrderItems(o.getOrderId())));
        return orders;
    }

    private List<OrderQueryDto> findOrders() {

        return entityManager.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address)"
                        + " from Order o"
                        + " join o.member m"
                        + " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }
    private List<OrderItemQueryDto> findOrderItems(long orderId) {
        return entityManager.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count)"
                                + " from OrderItem oi"
                                + " join oi.item i"
                                + " where oi.order.id = :orderId",OrderItemQueryDto.class)
                .setParameter("orderId",orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> orders = findOrders();

        Map<Long, List<OrderItemQueryDto>> results = findOrderItemMap(toOrderIds(orders));

        orders.stream().forEach((o) -> o.setOrderItems(results.get(o.getOrderId())));
        return orders;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> orders) {
        return orders.stream()
                .map((o) -> o.getOrderId())
                .collect(Collectors.toList());
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = entityManager.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count)"
                                + " from OrderItem oi"
                                + " join oi.item i"
                                + " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        return orderItems.stream().collect(groupingBy(OrderItemQueryDto::getOrderId));

    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return entityManager.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id,m.name,o.orderDate,o.status,d.address,i.name,oi.orderPrice,oi.count)"
                                + " from Order o"
                                + " join o.member m"
                                + " join o.delivery d"
                                + " join o.orderItems oi"
                                + " join oi.item i", OrderFlatDto.class)
                        .getResultList();
    }
}
