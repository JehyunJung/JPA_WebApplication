package jpabook.jpashop.web;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/new")
    public String createItem(Model model) {
        BookForm bookForm = new BookForm();
        model.addAttribute("form", bookForm);
        return "items/createItemForm.html";
    }

    @PostMapping("/new")
    public String create(BookForm form) {
        Book book = new Book();

        book.setName(form.getName());
        book.setStockQuantity(form.getStockQuantity());
        book.setPrice(form.getPrice());

        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping("/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();

        form.setId(itemId);
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setIsbn(item.getIsbn());
        form.setAuthor(item.getAuthor());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@ModelAttribute("form") BookForm bookForm, BindingResult bindingResult) {
        /*
        //Merge 활용
        Book book = new Book();
        book.setId(bookForm.getId());
        book.setName(bookForm.getName());
        book.setStockQuantity(bookForm.getStockQuantity());
        book.setPrice(bookForm.getPrice());
        book.setAuthor(bookForm.getAuthor());
        book.setIsbn(bookForm.getIsbn());
        itemService.saveItem(book);*/
        //변경감지를 활용
        itemService.updateItem(bookForm.getId(), bookForm.getName(),bookForm.getPrice(), bookForm.getStockQuantity());
        return "redirect:/items";
    }
}
