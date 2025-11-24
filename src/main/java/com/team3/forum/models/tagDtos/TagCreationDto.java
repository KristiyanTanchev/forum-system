package com.team3.forum.models.tagDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagCreationDto {

    @NotBlank(message = "Tag name is required.")
    @Size(min = 2,max = 50, message = "Tag name must be between 2 and 50 character")
    private String name;
}
