package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("상품 주문 테스트")
    void order() {
        //given
        //Member 객체 생성
        Member member = createMember();
        //Book 객체 생성
        Book book = createBook();

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order savedOrder = orderRepository.findOne(orderId);

        //then
        //상품 주문시 주문의 상태는 OrderStatus.ORDER이어야한다.
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        //주문한 상품의 종류와 총주문액 일치해야한다.
        assertThat(savedOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(savedOrder.getTotalPrice()).isEqualTo(book.getPrice() * orderCount);
        //주문 이후에 상품의 재고가 줄어야한다.
        assertThat(book.getStockQuantity()).isEqualTo(8);
    }

    @Test
    @DisplayName("재고 수량을 초과한 주문 테스트")
    void invalidateOrder() {
        //given
        //Member 객체 생성
        Member member = createMember();
        //Book 객체 생성
        Book book = createBook();

        //when
        int orderCount = 15;

        //then
        assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class);

    }

    @Test
    @DisplayName("상품 취소 테스트")
    void cancelOrder() {
        //given
        //Member 객체 생성
        Member member = createMember();
        //Book 객체 생성
        Book book = createBook();

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        orderService.cancelOrder(orderId);

        Order savedOrder = orderRepository.findOne(orderId);

        //then
        //주문을 취소하면 주문의 상태는 OrderStatus.CANCEL 이어야한다.
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        //주문을 취소하면 상품의 재고가 복구되야한다.
        assertThat(book.getStockQuantity()).isEqualTo(10);
    }



    private Member createMember(){
        Member member = new Member();
        member.setName("user1");
        member.setAddress(new Address("창원","상남동","12345"));
        entityManager.persist(member);
        return member;
    }

    private Book createBook() {
        Book book = new Book();
        book.setName("JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        entityManager.persist(book);
        return book;
    }
}