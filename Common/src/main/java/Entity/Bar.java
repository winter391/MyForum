package Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("bar")
public class Bar
{
    private Long id;

    private String name;

    private String description;

    private String coverImage;

    private Integer memberCount;

    private Integer postCount;

    private Long masterId;

    private String masterNickname;

    private Integer isBanned;

    private Integer postPermission;
}