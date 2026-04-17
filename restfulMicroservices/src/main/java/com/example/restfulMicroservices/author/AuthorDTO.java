package com.example.restfulMicroservices.author;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class AuthorDTO {

    @NotNull
    private String name;
    @NotNull
    private String url;
}
