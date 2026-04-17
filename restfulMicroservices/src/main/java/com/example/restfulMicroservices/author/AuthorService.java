package com.example.restfulMicroservices.author;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public boolean createAuthor(AuthorDTO authorDTO){

        try {
            Author author =  Author.builder()
                    .name(authorDTO.getName())
                    .url(authorDTO.getUrl()).build();

            authorRepository.save(author);
        }catch (Exception exception){
            log.error(exception.toString());
            return false;
        }

        return true;
    }

    public AuthorDTO findByNameAndUrl(String name, String url){
        Author authorFromDatabase = authorRepository.findByNameAndUrl(name, url);
        return AuthorDTO.builder()
                .name(authorFromDatabase.getName())
                .url(authorFromDatabase.getUrl())
                .build();
    }

    public List<AuthorDTO> findAll(){
        List<Author> authorList = new ArrayList<>();
        authorRepository.findAll().forEach(authorList::add);

        List<AuthorDTO> authorDTOList = new ArrayList<>();
        authorList.forEach(e->{
            authorDTOList.add(AuthorDTO.builder().name(e.getName()).url(e.getUrl()).build());
        });

        return authorDTOList;
    }

}
