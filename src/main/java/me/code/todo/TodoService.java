package me.code.todo;

import java.util.Collection;

public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public Todo create(String name, String description) {
        var existing = repository.getByName(name);
        if (existing.isPresent()) {
            return null;
        }

        var todo = new Todo(name, description, false);
        repository.save(todo);

        return todo;
    }

    public Todo remove(String name) {
        var todo = repository.getByName(name);

        if (todo.isEmpty()) {
            return null;
        } else {
            repository.remove(todo.get());
            return todo.get();
        }
    }

    public Collection<Todo> getAll() {
        return repository.getAll();
    }

}
