package thoqbk.springtesting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import thoqbk.springtesting.dtos.CreateTodoDto;
import thoqbk.springtesting.dtos.TodoDto;
import thoqbk.springtesting.service.TodoService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TodoController {
    @Autowired
    private TodoService todoService;

    @RequestMapping(value = "/api/todos", method = RequestMethod.GET)
    public List<TodoDto> allTodos() {
        return todoService.findAll().stream().map(todo -> {
            TodoDto dto = new TodoDto();
            dto.setId(todo.getId());
            dto.setName(todo.getName());
            return dto;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/api/todos", method = RequestMethod.POST)
    public TodoDto create(@RequestBody CreateTodoDto request) {
        var todo = todoService.create(request.getName());
        var retVal = new TodoDto();
        retVal.setId(todo.getId());
        retVal.setName(todo.getName());

        return retVal;
    }
}
