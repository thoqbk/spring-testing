package thoqbk.springtesting.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import thoqbk.springtesting.dtos.CreateTodoDto;
import thoqbk.springtesting.dtos.TodoDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for Todo APIs
 *
 * This test class uses @SpringBootTest to start the whole application-context and exposes
 * APIs at an available and random port.
 *
 * H2 in-memory DB also starts with initial data defined in schema.sql and data.sql.
 *
 * This is called integration test because we are testing multiple layers (e.g. controller, service and repository)
 * at the same time.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void getAllTodos_returnsCorrectResult() {
        // arrange
        var request = new CreateTodoDto();
        request.setName("todo 123424");
        var createResponse = restTemplate.exchange("/api/todos", HttpMethod.POST, new HttpEntity<>(request), TodoDto.class);
        var newTodo = createResponse.getBody();

        // act
        var responseType = new ParameterizedTypeReference<List<TodoDto>>(){};
        var response = restTemplate.exchange("/api/todos", HttpMethod.GET, new HttpEntity<>(null), responseType);

        // assert
        var found = response.getBody().stream()
                .filter(td -> td.getId() == newTodo.getId() && "todo 123424".equals(td.getName()))
                .count();
        assertThat(found).isEqualTo(1);
    }
}
