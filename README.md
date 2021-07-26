# Using correct annotations in Spring testing

## 1. Introduction

There's heavily use of annotations in Spring Framework. This makes confusing about what annotations should be used especially in tests. It sometimes ends up with adding redundant annotations or the tests not work as expected. On stackoverflow, you can easily find the developers make [mistakes][4] with Spring annotations or someone who makes things work but not fully understand why.

To make thing clear, in this article, I would summarize the annotations we should use in each testing senario using JUnit 5 and Spring Test Framework

## 2. Spring Framework and Spring Boot

Before we look into Spring test, we need to know how Spring works and the relationship between Spring Framework and Spring Boot.

Spring is the most popular application framework of Java. It simplifies the Java EE development by providing dependency injection feature and supports of many popular technologies such as Spring JDBC, Spring MVC, Spring Test.

To start a Spring application, you need to create an ApplicationContext which is an IoC container of all bean objects using in the application. Here is an example:

```java
@Configuration
// This is the primary configuration class of the application. From here, Spring
// will scan all declared components and make them available in ApplicationContext.
// We can use @Import(OtherConfig.class) to import other configurations
// into the primary one.
// We can also use @ComponentScans (e.g. @ComponentScans("services"))
// to ask Spring to scan for all components (e.g. classes having @Component, @Service)
// in a package and add them into the service container (i.e. ApplicationContext)
public class Config {
    @Bean
    public HelloService helloService() {
        // assume we have HelloService interface and HelloServiceImpl class
        return new HelloServiceImp();
    }
}

public class MainApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Config.class);
        // Use container API to get back the service we need
        var helloService = context.getBean(HelloService.class);
        System.out.print(helloService.hi());
    }
}
```

From the example above you can foresee that we need to do many manual steps to get a Spring application up and running, especially the enterprise application with many external dependencies such as DB, message queue, third-party APIs.

Spring Boot makes things easier by doing all auto configrations for us. Here is the code for the same example in Spring Boot:

```java
@SpringBootApplication
public class MainApplication {
    @Bean
    public HelloService helloService() {
        return new HelloServiceImp();
    }

    public static void main(String[] args) {
        Application context = SpringApplication.run(MainApplication.class);
        var helloService = context.getBean(HelloService.class);
        System.out.println(helloService.hi());
    }
}
```

Looking into [SpringBootApplication][1] annotation you can see that there is a meta annotation @SpringBootConfiguration which again includes @Configuration. That explains why Spring can still find the primary configuration class and load HelloService bean with `@SpringBootApplication`. 

Spring tends to group multiple annotations into one to make things simpler. This grouping generates many new annotations which sometimes makes developers confusing and adding redandunt annotations (e.g. adding both @SpringBootApplication and @Configuration to the same class)

Visit following links in case you want to learn more about Spring Framework and Spring Boot:
- [Understanding the Basics of Spring vs. Spring Boot][2]
- [A Comparison Between Spring and Spring Boot][3]

Overall, we need to have 1 primary configuration to create an ApplicationContext for any Spring application.

## 3. Write a basic unit-test in Spring Framework

Following is the most basic setup to write a test in Spring:

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={TestConfig.class})
public class TodoServiceTest {
    @Autowired
    private TodoService todoService;
    // ...
}
```

With this code, a Spring ApplicationContext will be created using TestConfig as the primary configuration. It then gets TodoService instance from the container and inject into the test class. This matches with what we discussed so far: Spring application needs to create ApplicationContext using 1 primary configuration.

There's a short form for the setup above which if you look inside the @SpringJUnitConfig you can see it includes 2 meta annotations: @ExtendWith and @ContextConfiguration

```java
@SpringJUnitConfig(TestConfig.class)
public class TodoServiceTest {
    // ...
}
```

If the configuration class is not spepcified, Spring will look for configuration embedded in test class

```java
@SpringJUnitConfig
public class TodoServiceTest {
    @Configuration
    static class TestConfig {
        @Bean
        public TodoService todoService() {
            // ...
        }
    }
    // ...
}
```

Notice that, in both cases, ApplicationContext is instantiated only once and shared among all the test methods in the class.

Using @TestPropertySource to pass in custom properties for testing. Has higher precedence than sources

```java
@SpringJUnitConfig(TestConfig.class)
@TestPropertySource(properties={"username=foo", "password=bar"},
    locations="classpath:todo-test.properties")
public class TodoServiceTests {
    // ...
}
```

Click [here][5] to view full code.

## 4. Testing with Spring Boot

Spring Boot introduces new annotations. Followings are some popular ones:

- @SpringBootTest
- @WebMvcTest
- @DataJpaTest, @DataJdbcTest, @JdbcTest
- @MockBean

### 4.1 Integration test with @SpringBootTest

Unlike @SpringJUnitConfig, @SpringBootTest , by default, starts the whole application context same as when you run your Spring Boot application. With this annotation, Spring will search for class with @SpringBootConfiguration and use it as the primary configuration to create ApplicationContext. It also does the auto configuration for TestRestTemplate which we can wire into test class and use to call APIs. Following is an example:

```java
@SpringBootApplication // includes meta annotation @SpringBootConfiguration
public class SpringtestingApplication {
    // ...
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoControllerTest {
    @Autowired
    TestRestTemplate restTemplate;
    // ...
}
```

Click [here][6] to view full code.

### 4.2 Unit test for controller layer with @WebMvcTest

Tests with @WebMvcTest will apply only configuration relevant to MVC tests. The full-configuration will be disable. Spring Test Framework also auto configures MockMvc which we can inject into test class and use it to call tested APIs.

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

Click [here][7] to view full code

### 5. Conclusion

To summarize:
- Use @SpringJUnitConfig to write unit-test
- Use @WebMvcTest with MockMvc to write unit-test for controller layer
- Use @SpringBootTest with TestRestTemplate to write integration-test for Spring Boot application.

[1]: https://docs.spring.io/spring-Boot/docs/current/api/org/springframework/Boot/autoconfigure/SpringBootApplication.html
[2]: https://dzone.com/articles/understanding-the-basics-of-spring-vs-spring-Boot
[3]: https://www.baeldung.com/spring-vs-spring-Boot
[4]: https://stackoverflow.com/questions/56289179/how-to-use-mockbean-with-junit-5-in-spring-Boot
[5]: src/test/java/thoqbk/springtesting/unit/TodoServiceTest.java
[6]: src/test/java/thoqbk/springtesting/integration/TodoControllerTest.java
[7]: src/test/java/thoqbk/springtesting/unit/TodoControllerTest.java
