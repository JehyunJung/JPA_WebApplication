package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.hibernate.annotations.DialectOverride;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;


    @Test
    @DisplayName("상품을 저장하는 테스트")
    void saveItem() {
        //given
        Book book = new Book();
        book.setName("book1");
        book.setAuthor("author1");
        book.setIsbn("1234");

        //when
        long savedId = itemService.saveItem(book);
        Book savedBook =(Book)itemService.findOne(savedId);
        //then
        assertThat(savedBook).isEqualTo(book);
    }

    @Test
    @DisplayName("상품 목록을 조회하는 테스트")
    void findItems(){
        //given
        Book book1 = new Book();
        book1.setName("book1");
        book1.setAuthor("author1");
        book1.setIsbn("1234");

        Book book2 = new Book();
        book2.setName("book2");
        book2.setAuthor("author2");
        book2.setIsbn("1235");

        //when
        long savedId = itemService.saveItem(book1);
        long savedId2 = itemService.saveItem(book2);
        List<Item> items = itemService.findItems();
        //then
        assertThat(items.size()).isEqualTo(2);
    }
}