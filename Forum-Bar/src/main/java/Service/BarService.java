package Service;

import Dto.*;
import Entity.Bar;
import Entity.BarMember;
import com.baomidou.mybatisplus.spring.service.IService;

import java.io.InputStream;
import java.util.List;

public interface BarService extends IService<Bar>
{
    public void createBar(CreateBarDto dto);

    public void deleteBar(Long barId);

    public void updateBar(UpdateBarDto dto);

    public String uploadBarImage(InputStream inputStream, Long barId);

    public void joinBar(Long barId);

    public void leaveBar(Long barId);

    public void kickMember(BarMemberDto dto);

    public void setAdmin(BarMemberDto dto);

    public void removeAdmin(BarMemberDto dto);

    public void muteMember(BarMemberDto dto);

    public void unmuteMember(BarMemberDto dto);

    public void setPostPermission(SetPostPermissionDto dto);

    public Bar getBar(Long barId);

    public List<BarMember> getBarMembers(Long barId);

    public Boolean checkPostPermission(Long barId);
}
