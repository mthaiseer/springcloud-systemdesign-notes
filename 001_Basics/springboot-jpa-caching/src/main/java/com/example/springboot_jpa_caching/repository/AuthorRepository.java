package com.example.springboot_jpa_caching.repository;

import com.example.springboot_jpa_caching.entity.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
}
