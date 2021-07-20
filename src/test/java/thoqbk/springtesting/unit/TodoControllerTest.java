package thoqbk.springtesting.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import thoqbk.springtesting.controllers.TodoController;
import thoqbk.springtesting.entity.Todo;
import thoqbk.springtesting.service.TodoService;
import thoqbk.springtesting.service.TodoServiceImpl;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit-tests for TodoController
 *
 * Using @WebMvcTest when you want to test controller layer only.
 *
 * When seeing this annotation, disable full auto-configuration (e.g. auto scan beans)
 * and Spring will auto-configure MockMvc.
 *
 */
@WebMvcTest(TodoController.class)
public class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    public void getAllTodos_returnsCorrectList() throws Exception {
        // arrange
        var todo = new Todo();
        todo.setId(123123);
        todo.setName("hello 23143");

        when(todoService.findAll()).thenReturn(Arrays.asList(todo));

        // act
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(123123)))
                .andExpect(jsonPath("$[0].name", is("hello 23143")));
    }
}
