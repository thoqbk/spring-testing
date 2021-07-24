# Spring Testing

## Introduction

### Testing in Spring Framework

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={SystemTestConfig.class})
public class TransferServiceTest {
    // ...
}
```

Or shorter:
```java
@SpringJUnitConfig(SystemTestConfig.class)
public class TransferServiceTest {
    // ...
}
```

If the config class is not spepcified, Spring will look for configuration embedded in test class
```java
@SpringJUnitConfig
public class TransferServiceTest {
    @Configuration
    @Import(SystemTestConfig.class)
    static class TestConfiguration {
        @Bean
        public DataSource dataSource() {
            // ...
        }
    }
}
```

Notice that, in both cases, ApplicationContext is instantiated only once and shared among test methods in the same class.

Using @TestPropertySource to pass in custom properties for testing. Has higher precedence than sources

```java
@SpringJUnitConfig(SystemTestConfig.class)
@TestPropertySource(properties={"username=foo", "password=bar"},
    locations="classpath:transfer-test.properties")
public class TransferServiceTests {
    // ...
}
```

### Testing with Spring Boot

Spring Boot introduces new annotations:

- @SpringBootTest
- @WebMvcTest
- @DataJpaTest, @DataJdbcTest, @JdbcTest
- @MockBean

@SpringBootTest

- Start the whole application context
- Search for @SpringBootConfiguration (@SpringBootApplication). An alternative to @ContextConfiguration
- Use @ContextConfiguration for slice testing
- Auto configured with TestRestTemplate
- No need to use @ExtendWith as it's a meta annotation of @SpringBootTest

Slicing test:

- Perform isolated testing within a slice of an application. Dependencies need to be mocked
- @WebMvcTest:
	- Disable full auto-configuration. Apply only configuration relevant to MVC tests
	- Auto configured MockMvc
	- Used in combination with @MockBean

## Writing tests for todo app

### Unit tests for service layer

Use @SpringJUnitConfig when you want to control the beans that will be created in your test.

@SpringJUnitConfig includes 2 meta annotations: @ExtendWith(SpringExtension.class) and @ContextConfiguration

We can configure test class with @SpringJUnitConfig in 2 ways:
1. Pass in the root configuration for application-context @SpringJUnitConfig(SystemTestConfig.class. This is equivalent to
```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={SystemTestConfig.class})
```
2. Without passing in configuration class e.g. @SpringJUnitConfig. In this case, Spring will search for a class that is marked with @Configuration and initialize application-context from here.


```java
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
    // ...
}
```

Test method:

```java
@SpringJUnitConfig
public class TodoServiceTest {
    // ...
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
```

### Unit-test for controller layer

Using @WebMvcTest when you want to test controller layer only.

When seeing this annotation, disable full auto-configuration (e.g. auto scan beans) and Spring will auto-configure MockMvc.

```java
@WebMvcTest(TodoController.class)
public class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;
    // ...
}
```

Test method:

```java
@WebMvcTest(TodoController.class)
public class TodoControllerTest {
    // ...
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
```

### Integration test

This test class uses @SpringBootTest to start the whole application-context and exposes APIs at an available and random port.

H2 in-memory DB also starts with initial data defined in schema.sql and data.sql.

This is called integration test because we are testing multiple layers (e.g controller, service and repository) at the same time.

```java
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
```

