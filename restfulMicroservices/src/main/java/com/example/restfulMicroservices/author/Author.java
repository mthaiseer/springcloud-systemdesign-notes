package com.example.restfulMicroservices.author;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "author", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "url"}))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String url;
}
