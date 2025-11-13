package com.team3.forum.models.tagDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagUpdateDto {

    @NotBlank(message = "Tag name cannot be blank.")
    @Size(max = 50, message = "Tag name must be up to 50 characters.")
    private String name;
}
