package ru.javabegin.backend.todo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javabegin.backend.todo.entity.Priority;
import ru.javabegin.backend.todo.repo.PriorityRepository;

import java.util.List;

@Service
@Transactional
public class PriorityService {
    private final PriorityRepository repository;

    public PriorityService(PriorityRepository repository){
        this.repository = repository;
    }


    public List<Priority> findAll(String title){
        return repository.findByPriorityTitle(title);
    }

    public Priority add(Priority priority){
        return repository.save(priority);
    }

    public Priority update(Priority priority){
        return repository.save(priority);
    }
}
