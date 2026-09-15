package Service.Impl;

import Code.UserCode;
import Dto.*;
import Code.BarCode;
import Entity.Bar;
import Entity.BarMember;
import Entity.PublishedPost;
import Entity.UserSession;
import Exception.GlobalException;
import Mapper.BarMapper;
import Mapper.BarMemberMapper;
import Mapper.PublishedPostMapper;
import Service.BarService;
import Util.GetUser;
import Util.OssUtil;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Service
public class BarServiceImpl extends ServiceImpl<BarMapper, Bar> implements BarService
{

    @Autowired
    private BarMemberMapper barMemberMapper;

    @Autowired
    private PublishedPostMapper publishedPostMapper;


    @Override
    @Transactional
    public void createBar(CreateBarDto dto)
    {
        UserSession session = GetUser.getUser();
        if(session.getIsBanned().equals(UserCode.banned))
        {
            throw new GlobalException("您已被封禁，无法创建贴吧");
        }
        Long oldId = getBaseMapper().getBarIdByName(dto.getName());
        if (oldId != null)
        {
            throw new GlobalException("已存在同名贴吧");
        }
        Bar bar = new Bar();
        bar.setName(dto.getName());
        bar.setDescription(dto.getDescription());
        bar.setMasterId(session.getId());
        bar.setMasterNickname(session.getNickName());
        bar.setMemberCount(1);
        bar.setPostPermission(0);
        save(bar);
        BarMember member = new BarMember();
        member.setBarId(bar.getId());
        member.setUserId(session.getId());
        member.setIdentity(BarCode.IDENTITY_MASTER);
        member.setIsBanned(UserCode.NotBanned);
        barMemberMapper.insert(member);
    }

    @Override
    @Transactional
    public void deleteBar(Long barId)
    {
        UserSession session = GetUser.getUser();
        Bar bar = getBarOrThrow(barId);
        if (!bar.getMasterId().equals(session.getId()))
        {
            throw new GlobalException("您没有权限删除该贴吧");
        }
        removeById(barId);
        barMemberMapper.deleteByBarId(barId);
        OssUtil.ForEachDelete(new HashSet<>(), "Bar/" + barId + "/");
    }

    @Override
    @Transactional
    public void updateBar(UpdateBarDto dto)
    {
        UserSession session = GetUser.getUser();
        Bar bar = getBarOrThrow(dto.getId());
        checkBarNotBanned(bar);
        if (!bar.getMasterId().equals(session.getId()))
        {
            throw new GlobalException("您没有权限修改该贴吧");
        }
        if (dto.getName() != null && !dto.getName().equals(bar.getName()))
        {
            Long oldId = getBaseMapper().getBarIdByName(dto.getName());
            if (oldId != null && !oldId.equals(bar.getId()))
            {
                throw new GlobalException("已存在同名贴吧");
            }
            bar.setName(dto.getName());
        }
        if (dto.getDescription() != null)
        {
            bar.setDescription(dto.getDescription());
        }
        if (dto.getCoverImage() != null)
        {
            bar.setCoverImage(dto.getCoverImage());
        }
        updateById(bar);
    }

    @Override
    @Transactional
    public String uploadBarImage(InputStream inputStream, Long barId)
    {
        UserSession session = GetUser.getUser();
        Bar bar = getBarOrThrow(barId);
        checkBarNotBanned(bar);
        if (!bar.getMasterId().equals(session.getId()))
        {
            throw new GlobalException("您没有权限上传该文件");
        }
        String fileName = Long.valueOf(System.currentTimeMillis()).toString() + ".jpg";
        String filePath = "Bar/" + barId.toString() + "/";
        String res = OssUtil.upLoadFile(inputStream, fileName, filePath);
        if (res == null)
        {
            throw new GlobalException("文件上传失败");
        }
        bar.setCoverImage(res);
        updateById(bar);
        return res;
    }

    @Override
    @Transactional
    public void joinBar(Long barId)
    {
        UserSession session = GetUser.getUser();
        Bar bar = getBarOrThrow(barId);
        checkBarNotBanned(bar);
        if (barMemberMapper.getBarMember(barId, session.getId()) != null)
        {
            throw new GlobalException("您已加入该贴吧");
        }
        BarMember member = new BarMember();
        member.setBarId(barId);
        member.setUserId(session.getId());
        member.setIdentity(BarCode.IDENTITY_MEMBER);
        member.setIsBanned(0);
        barMemberMapper.insert(member);
        getBaseMapper().updateMemberCount(barId, 1);
    }

    @Override
    @Transactional
    public void leaveBar(Long barId)
    {
        UserSession session = GetUser.getUser();
        Bar bar = getBarOrThrow(barId);
        checkBarNotBanned(bar);
        BarMember member = getMemberOrThrow(barId, session.getId());
        if (member.getIdentity() >= BarCode.IDENTITY_ADMIN)
        {
            throw new GlobalException("管理员和吧主不能直接退出贴吧，请先转让或删除贴吧");
        }
        barMemberMapper.deleteById(member.getId());
        getBaseMapper().updateMemberCount(barId, -1);
    }

    @Override
    @Transactional
    public void kickMember(BarMemberDto dto)
    {
        UserSession session = GetUser.getUser();
        Bar bar = getBarOrThrow(dto.getBarId());
        checkBarNotBanned(bar);
        BarMember operator = getMemberOrThrow(dto.getBarId(), session.getId());
        if (operator.getIdentity() < BarCode.IDENTITY_ADMIN)
        {
            throw new GlobalException("您没有权限移出成员");
        }
        BarMember target = getMemberOrThrow(dto.getBarId(), dto.getUserId());
        if (target.getUserId().equals(session.getId()))
        {
            throw new GlobalException("不能移出自己，退出贴吧请使用退出接口");
        }
        if (target.getIdentity() >= operator.getIdentity())
        {
            throw new GlobalException("您的权限不足，无法移出该成员");
        }
        barMemberMapper.deleteById(target.getId());
        getBaseMapper().updateMemberCount(dto.getBarId(), -1);
    }

    @Override
    @Transactional
    public void setAdmin(BarMemberDto dto)
    {
        UserSession session = GetUser.getUser();
        checkMaster(dto.getBarId(), session.getId());
        BarMember target = getMemberOrThrow(dto.getBarId(), dto.getUserId());
        if (target.getIdentity() != BarCode.IDENTITY_MEMBER)
        {
            throw new GlobalException("该用户不是普通成员，无法设置为管理员");
        }
        target.setIdentity(BarCode.IDENTITY_ADMIN);
        barMemberMapper.updateById(target);
    }

    @Override
    @Transactional
    public void removeAdmin(BarMemberDto dto)
    {
        UserSession session = GetUser.getUser();
        checkMaster(dto.getBarId(), session.getId());
        BarMember target = getMemberOrThrow(dto.getBarId(), dto.getUserId());
        if (target.getIdentity() != BarCode.IDENTITY_ADMIN)
        {
            throw new GlobalException("该用户不是管理员");
        }
        target.setIdentity(BarCode.IDENTITY_MEMBER);
        barMemberMapper.updateById(target);
    }

    @Override
    @Transactional
    public void muteMember(BarMemberDto dto)
    {
        UserSession session = GetUser.getUser();
        BarMember operator = getMemberOrThrow(dto.getBarId(), session.getId());
        if (operator.getIdentity() < BarCode.IDENTITY_ADMIN)
        {
            throw new GlobalException("您没有权限禁言成员");
        }
        BarMember target = getMemberOrThrow(dto.getBarId(), dto.getUserId());
        if (target.getIdentity() >= operator.getIdentity())
        {
            throw new GlobalException("您的权限不足，无法禁言该成员");
        }
        target.setIsBanned(1);
        barMemberMapper.updateById(target);
    }

    @Override
    @Transactional
    public void unmuteMember(BarMemberDto dto)
    {
        UserSession session = GetUser.getUser();
        BarMember operator = getMemberOrThrow(dto.getBarId(), session.getId());
        if (operator.getIdentity() < BarCode.IDENTITY_ADMIN)
        {
            throw new GlobalException("您没有权限解除禁言");
        }
        BarMember target = getMemberOrThrow(dto.getBarId(), dto.getUserId());
        if (target.getIdentity() >= operator.getIdentity())
        {
            throw new GlobalException("您的权限不足，无法解除该成员的禁言");
        }
        target.setIsBanned(0);
        barMemberMapper.updateById(target);
    }

    @Override
    @Transactional
    public void setPostPin(SetPostPinDto dto)
    {
        UserSession session = GetUser.getUser();
        BarMember operator = getMemberOrThrow(dto.getBarId(), session.getId());
        if (operator.getIdentity() < BarCode.IDENTITY_ADMIN)
        {
            throw new GlobalException("您没有权限置顶或取消置顶该帖子");
        }
        PublishedPost post = publishedPostMapper.selectById(dto.getPostId());
        if (post == null)
        {
            throw new GlobalException("该帖子不存在");
        }
        if (!dto.getBarId().equals(post.getBarId()))
        {
            throw new GlobalException("该帖子不属于该贴吧");
        }
        post.setPin(dto.getPin());
        publishedPostMapper.updateById(post);
    }

    @Override
    @Transactional
    public void setPostPermission(SetPostPermissionDto dto)
    {
        UserSession session = GetUser.getUser();
        checkMaster(dto.getBarId(), session.getId());
        Bar bar = getBarOrThrow(dto.getBarId());
        bar.setPostPermission(dto.getPostPermission());
        updateById(bar);
    }

    @Override
    public Bar getBar(Long barId)
    {
        return getBarOrThrow(barId);
    }

    @Override
    public List<BarMember> getBarMembers(Long barId)
    {
        Bar bar = getBarOrThrow(barId);
        checkBarNotBanned(bar);
        List<BarMember> members = barMemberMapper.getBarMembersByBarId(barId);
        if (members == null)
        {
            return new ArrayList<>();
        }
        return members;
    }

    @Override
    public Boolean checkPostPermission(Long barId)
    {
        UserSession session = GetUser.getUser();
        Bar bar = getBarOrThrow(barId);
        checkBarNotBanned(bar);
        BarMember member = barMemberMapper.getBarMember(barId, session.getId());
        if (member != null)
        {
            if (Integer.valueOf(1).equals(member.getIsBanned()))
            {
                throw new GlobalException("您在该贴吧内被禁言");
            }
            return true;
        }
        if (Integer.valueOf(1).equals(bar.getPostPermission()))
        {
            throw new GlobalException("该贴吧只有成员才能发帖");
        }
        return true;
    }


    private Bar getBarOrThrow(Long barId)
    {
        Bar bar = getById(barId);
        if (bar == null)
        {
            throw new GlobalException("该贴吧不存在");
        }
        return bar;
    }

    private BarMember getMemberOrThrow(Long barId, Long userId)
    {
        BarMember member = barMemberMapper.getBarMember(barId, userId);
        if (member == null)
        {
            throw new GlobalException("该用户不是该贴吧的成员");
        }
        return member;
    }

    private void checkMaster(Long barId, Long userId)
    {
        BarMember member = getMemberOrThrow(barId, userId);
        if (member.getIdentity() != BarCode.IDENTITY_MASTER)
        {
            throw new GlobalException("只有吧主可以进行该操作");
        }
    }

    private void checkBarNotBanned(Bar bar)
    {
        if (Integer.valueOf(1).equals(bar.getIsBanned()))
        {
            throw new GlobalException("该贴吧已被封禁");
        }
    }
}
