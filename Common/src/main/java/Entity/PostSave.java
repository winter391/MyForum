package Entity;

import lombok.Data;

import java.util.List;


@Data
public class PostSave
{
    private String title;
    private List<PostSaveBlock> blocks;
}
