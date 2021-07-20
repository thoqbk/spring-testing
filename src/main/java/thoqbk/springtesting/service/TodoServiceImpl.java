package thoqbk.springtesting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thoqbk.springtesting.entity.Todo;
import thoqbk.springtesting.repository.TodoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {
    @Autowired
    private TodoRepository todoRepository;

    @Override
    public List<Todo> findAll() {
        List<Todo> retVal = new ArrayList<>();
        todoRepository.findAll().iterator().forEachRemaining(retVal::add);
        return retVal;
    }

    @Override
    public Todo create(String name) {
        Todo todo = new Todo();
        todo.setName(name);
        return todoRepository.save(todo);
    }
}
