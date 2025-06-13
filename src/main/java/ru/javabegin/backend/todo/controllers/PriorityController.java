package ru.javabegin.backend.todo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.javabegin.backend.todo.entity.Priority;
import ru.javabegin.backend.todo.entity.User;
import ru.javabegin.backend.todo.service.PriorityService;
import ru.javabegin.backend.todo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/priority")
public class PriorityController {
    private PriorityService priorityService;
    private UserService userService;

    public PriorityController(PriorityService priorityService, UserService userService){
        this.priorityService = priorityService;
        this.userService = new UserService();
    }

    @PostMapping("/all")
    public List<Priority> findAll(@RequestBody String title){
        return priorityService.findAll(title);
    }

    @PostMapping("/add")
    public ResponseEntity<Priority> add(@RequestBody Priority priority){
        if(priority.getId() != null && priority.getId() != 0)
            return new ResponseEntity("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        if(priority.getTitle() == null || priority.getTitle().trim().length() == 0)
            return new ResponseEntity("missing param: title", HttpStatus.NOT_ACCEPTABLE);
        return ResponseEntity.ok(priorityService.add(priority));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Priority priority, @RequestHeader("user_id") Long userId) {

        // Проверка обязательных полей Priority
        if (priority.getId() == null || priority.getId() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("missing param: id");
        }
        if (priority.getTitle() == null || priority.getTitle().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("missing param: title");
        }

        // Проверка существования пользователя
        User user = userService.search(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user not found");
        }

        // Проверка, принадлежит ли Priority этому пользователю
        Priority existingPriority = priorityService.findById(priority.getId());
        if (existingPriority == null || !existingPriority.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("priority doesn't belong to user");
        }

        // Обновление Priority
        priority.setUser(user); // Устанавливаем связь
        return ResponseEntity.ok(priorityService.update(priority));
    }
}
