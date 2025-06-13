package ru.javabegin.backend.todo.controllers;
/*

Используем @RestController вместо обычного @Controller, чтобы все ответы сразу оборачивались в JSON,
иначе пришлось бы добавлять лишние объекты в код, использовать @ResponseBody для ответа, указывать тип отправки JSON

Названия методов могут быть любыми, главное не дублировать их имена внутри класса и URL mapping

*/

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.javabegin.backend.todo.entity.Category;
import ru.javabegin.backend.todo.search.CategorySearchValues;
import ru.javabegin.backend.todo.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/category") // базовый URI
public class CategoryController {

    // доступ к данным из БД
    private CategoryService categoryService;

    // автоматическое внедрение экземпляра класса через конструктор
    // не используем @Autowired ля переменной класса, т.к. "Field injection is not recommended "
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping("/all")
    public List<Category> findAll(@RequestBody String email) {
        return categoryService.findAll(email);
    }

    @PostMapping("/add")
    public ResponseEntity<Category> add(@RequestBody Category category) {
        if (category.getId() != null && category.getId() != 0) {
            return new ResponseEntity("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        }
        if (category.getTitle() == null || category.getTitle().trim().length() == 0) {
            return new ResponseEntity("missing param: title", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(categoryService.add(category));
    }

    @PutMapping("/update")
    public ResponseEntity<Category> update(@RequestBody Category category) {
        if (category.getId() == null || category.getId() == 0) {
            return new ResponseEntity("missing param: id", HttpStatus.NOT_ACCEPTABLE);
        }
        if (category.getTitle() == null || category.getTitle().trim().length() == 0) {
            return new ResponseEntity("missing param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        categoryService.update(category);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Category> delete(@PathVariable("id") Long id) {
        try {
            categoryService.deleteById(id);
        }catch (EmptyResultDataAccessException e) {
            return new ResponseEntity("id = " + id + " not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity("Успешно удалено", HttpStatus.OK);
    }
    @PostMapping("/search")
    public ResponseEntity<List<Category>> search(@RequestBody CategorySearchValues categorySearchValues) {
        if (categorySearchValues.getEmail() == null || categorySearchValues.getEmail().trim().length() == 0) {
            return new ResponseEntity("missing param: email", HttpStatus.NOT_ACCEPTABLE);
        } else {
            List<Category> list = categoryService.search(categorySearchValues.getTitle(), categorySearchValues.getEmail());
            return ResponseEntity.ok(list);
        }
    }
}
