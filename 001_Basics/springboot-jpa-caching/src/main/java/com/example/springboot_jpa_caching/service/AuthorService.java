package com.example.springboot_jpa_caching.service;


import com.example.springboot_jpa_caching.entity.Author;
import com.example.springboot_jpa_caching.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public Author createAuthor(Author author){
        return authorRepository.save(author);
    }


    @Cacheable("authors")
    public List<Author> findAllAuthors() throws InterruptedException {
        Thread.sleep(3000);
        List<Author> authors = new ArrayList<>();
        authorRepository.findAll().forEach(authors::add);
        return authors;
    }
}
