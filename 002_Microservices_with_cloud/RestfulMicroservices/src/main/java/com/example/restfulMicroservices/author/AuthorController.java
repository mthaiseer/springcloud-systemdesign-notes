package com.example.restfulMicroservices.author;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/author")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @PostMapping
    public ResponseEntity<?> addAuthor(@RequestBody @Valid AuthorDTO authorDTO){
        log.info("Accessing add author api");
        try{
            boolean isAuthorCreated = authorService.createAuthor(authorDTO);
            if(!isAuthorCreated){
                throw new NoAuthorCreatedException("Author creation failed");
            }
        }catch (Exception ex){
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Created", HttpStatus.CREATED);
    }

    @GetMapping
    public List<AuthorDTO> findAllAuthors(){
        return authorService.findAll();
    }


}
