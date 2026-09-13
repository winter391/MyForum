package Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("bar_member")
public class BarMember
{
    private Long id;

    private Long barId;

    private Long userId;

    private Integer identity;

    private Integer isBanned;
}