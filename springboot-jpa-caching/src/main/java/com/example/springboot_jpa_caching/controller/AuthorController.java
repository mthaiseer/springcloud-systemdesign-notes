package com.example.springboot_jpa_caching.controller;

import com.example.springboot_jpa_caching.entity.Author;
import com.example.springboot_jpa_caching.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author")
public class AuthorController {


    @Autowired
    private AuthorService authorService;

    @PostMapping
    public Author createAuthor(@RequestBody Author author){
        return authorService.createAuthor(author);
    }

    @GetMapping
    public List<Author> findAllAuthors() throws InterruptedException {
        return authorService.findAllAuthors();
    }

}
