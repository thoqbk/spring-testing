package thoqbk.springtesting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import thoqbk.springtesting.entity.Todo;

@Repository
public interface TodoRepository extends CrudRepository<Todo, Integer> {
}
