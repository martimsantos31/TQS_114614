# a)

- A_EmployeeRepositoryTest.java, line 48
```java
assertThat(found).isNotNull()
                 .extracting(Employee::getName)
                 .isEqualTo(persistedAlex.getName());
```

- B_EmployeeRepositoryTest.java, line 115
```java
assertThat(allEmployees).hasSize(3)
                        .extracting(Employee::getName)
                        .contains(alex.getName(), john.getName(), bob.getName());
```

- D_EmployeeRestControllerIT.java, line 58
```java
assertThat(found).extracting(Employee::getName)
                 .containsOnly("bob");
```

- E_EmployeeRestControllerIT.java, line 67
```java
assertThat(response.getBody()).extracting(Employee::getName)
                              .containsExactly("bob", "alex");
```

# b)
1. **@BootstrapWith(SpringBootTestContextBootstrapper.class)** - Initializes the Spring Boot test context.
2. **@ExtendWith(SpringExtension.class)** - Integrates JUnit 5 with Spring.
3. **@Transactional** - Ensures each test runs within a transaction and is automatically rolled back.
4. **@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)** - Uses an in-memory database by default.
5. **@AutoConfigureDataJpa** - Automatically configures the necessary JPA beans.
6. **@Import(AutoConfiguredTestDatabaseRegistrar.class)** - Registers the test database configuration.
7. **@TypeExcludeFilters(DataJpaTypeExcludeFilter.class)** - Excludes unnecessary components, loading only JPA-related beans.

# c)
In the file, B_EmployeeService_UnitTest.java we use mock the behavior of the Repository.

# d)
So the @Mock annotation comes form the Mockito library, and it is used to create and inject mocked instances of a class.
When we use the @Mock annotation, we need to manually inject the mock, this annotation is used in unit tests where Spring
application context is not needed. The @MockBean annotation comes from Spring Boot and replaces the Spring Bean with a Mockito Mock
in the spring application contect, it's used in integration tests where the Spring application context is needed.

# e)
The application-integrationtest.properties file is a configuration file specifically used for integration tests. Provides
custom settings that override the application.properties when the tests are running. As i said before, it's used for integration tests.
Ensures a separate, isolated environment for the tests, so the tests don't interfere with the production environment.


# f)
| Strategy                            | Scope                             | Loads Full Context? | Uses Real HTTP Requests? | Best For |
|-------------------------------------|-----------------------------------|----------------------|--------------------------|----------|
| `@WebMvcTest`                       | Only Controllers                 | ❌ No                | ❌ No (Uses MockMvc)      | Unit testing controllers |
| `@SpringBootTest` + MockMvc         | Controllers + Services + Repository | ✅ Yes               | ❌ No (Uses MockMvc)      | Integration tests without real HTTP calls |
| `@SpringBootTest` + TestRestTemplate | Full API                          | ✅ Yes               | ✅ Yes                    | End-to-end API testing |