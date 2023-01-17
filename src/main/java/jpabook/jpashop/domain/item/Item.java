package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="dtype")
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "items", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();

    private int price;

    private int stockQuantity;

    //재고를 증가시키는 메소드
    public void addStock(int quantity) {
        stockQuantity += quantity;
    }

    //재고를 증가시키는 메소드
    public void removeStock(int quantity) {
        int restStock = stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
