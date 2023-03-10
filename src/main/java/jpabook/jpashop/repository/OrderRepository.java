package jpabook.jpashop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.QItem;
import jpabook.jpashop.repository.order.simplequery.SimpleOrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    /*
    //jpql
    public List<Order> findAll(OrderSearch orderSearch) {
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 필터링
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            }
            else{
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 필터링
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            }
            else{
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        //주문 상태 parameter 설정
        if (orderSearch.getOrderStatus() != null) {
            query.setParameter("status", orderSearch.getOrderStatus());
        }

        //회원 이름 필터링
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }
     */


    /*
    //JPACriteria
    public List<Order> findAll(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> order = cq.from(Order.class);
        Join<Order, Member> member = order.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 필터링
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(order.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 필터링
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(member.<String>get("name"), "%"+orderSearch.getMemberName()+"%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);

        return query.getResultList();
    }
    */


    //QueryDsl
    public List<Order> findAll(OrderSearch orderSearch) {
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        QDelivery delivery = QDelivery.delivery;
        QOrderItem orderItem = QOrderItem.orderItem;
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();
        //주문 상태 필터링
        if (orderSearch.getOrderStatus() != null) {
            builder.and(order.status.eq(orderSearch.getOrderStatus()));
        }

        //회원 이름 필터링
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            builder.and(member.name.like(orderSearch.getMemberName()));
        }

        JPAQueryFactory query = new JPAQueryFactory(em);


        List<Order> orders = query
                .select(order)
                .from(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery,delivery).fetchJoin()
                .join(order.orderItems,orderItem).fetchJoin()
                .join(orderItem.item,item).fetchJoin()
                .where(builder)
                .limit(1000)
                .fetch();

        return orders;
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o"
                        + " join fetch o.member m"
                        + " join fetch o.delivery d", Order.class)
                .getResultList();
    }
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o"
                        + " join fetch o.member m"
                        + " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.order.simplequery.SimpleOrderQueryDto(o.id,m.name,"
                + "o.orderDate, o.status, d.address)"
                + " from Order o"
                + " join o.member m"
                + " join o.delivery d", SimpleOrderQueryDto.class)
                .getResultList();
    }

    public List<Order> findAllWithItems() {
        return em.createQuery("select distinct o from Order o"
                + " join fetch o.member m"
                + " join fetch o.delivery d"
                + " join fetch o.orderItems oi"
                + " join fetch oi.item i", Order.class)
                /*.setFirstResult(0)
                .setMaxResults(100)*/
                .getResultList();
    }





}
