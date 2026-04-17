package com.example.restfulMicroservices.author;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {

    public Author findByNameAndUrl(String name, String url);
}
