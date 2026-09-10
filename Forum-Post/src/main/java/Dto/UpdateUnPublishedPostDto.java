package Dto;


import lombok.Data;

@Data
public class UpdateUnPublishedPostDto
{
    private UploadUnPublishedPostDto dto;

    private Long targetId;
}
