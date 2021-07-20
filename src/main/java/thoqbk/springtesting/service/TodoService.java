package thoqbk.springtesting.service;

import thoqbk.springtesting.entity.Todo;

import java.util.List;

public interface TodoService {
    List<Todo> findAll();

    Todo create(String name);
}
