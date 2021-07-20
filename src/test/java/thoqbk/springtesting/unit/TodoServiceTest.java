package thoqbk.springtesting.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import thoqbk.springtesting.entity.Todo;
import thoqbk.springtesting.repository.TodoRepository;
import thoqbk.springtesting.service.TodoService;
import thoqbk.springtesting.service.TodoServiceImpl;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit-tests for TodoService.
 *
 * Use @SpringJUnitConfig when you want to control the beans that will be created in your test.
 *
 * @SpringJUnitConfig includes 2 meta annotations: @ExtendWith(SpringExtension.class) and @ContextConfiguration
 *
 * We can configure test class with @SpringJUnitConfig in 2 ways:
 * 1. Pass in the root configuration for application-context @SpringJUnitConfig(SystemTestConfig.class). This is equivalent to
 * @ExtendWith(SpringExtension.class)
 * @ContextConfiguration(classes={SystemTestConfig.class})
 *
 * 2. Without passing in configuration class e.g. @SpringJUnitConfig. In this case, Spring will search for a class
 * that is marked with @Configuration and initialize application-context from here.
 */
@SpringJUnitConfig
public class TodoServiceTest {
    @Autowired
    private TodoService todoService;

    @MockBean
    private TodoRepository todoRepository;

    @Configuration
    static class Config {
        @Bean
        public TodoService todoService() {
            return new TodoServiceImpl();
        }
    }

    @Test
    public void findAll_returnsCorrectResult() {
        // arrange
        var todo = new Todo();
        todo.setId(23123);
        todo.setName("AB 123");

        when(todoRepository.findAll()).thenReturn(Arrays.asList(todo));

        // act
        var result = todoService.findAll();

        // assert
        var found = result.stream().filter(td -> td.getId() == 23123 && "AB 123".equals(td.getName())).count() > 0;
        assertThat(found).isTrue();
    }
}
