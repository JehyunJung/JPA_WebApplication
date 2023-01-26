package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
     static class InitService {
        private final EntityManager entityManager;

        public void dbInit1() {
            Member member = createMember("userA", "서울", "1", "1111");
            entityManager.persist(member);

            Book book1=createBook("JPA Book1", 10000, 100);
            entityManager.persist(book1);

            Book book2=createBook("JPA Book2", 20000, 100);
            entityManager.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 2);
            Order order = Order.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            entityManager.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB", "전주", "2", "2222");
            entityManager.persist(member);

            Book book1=createBook("Spring Book1", 20000, 200);
            entityManager.persist(book1);

            Book book2=createBook("Spring Book2", 40000, 300);
            entityManager.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 4);
            Order order = Order.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            entityManager.persist(order);
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);

            return book;
        }
        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setStatus(DeliveryStatus.READY);
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }


}
