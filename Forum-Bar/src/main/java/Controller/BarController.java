package Controller;

import Dto.*;
import Entity.Bar;
import Entity.BarMember;
import Result.Result;
import Result.ResultUtil;
import Service.BarService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/MyForum/Bar")
@Slf4j
public class BarController
{
    @Autowired
    private BarService barService;


    @PostMapping("/createBar")
    public Result<?> createBar(@RequestBody @Valid CreateBarDto dto)
    {
        barService.createBar(dto);
        return ResultUtil.success();
    }

    @PostMapping("/deleteBar")
    public Result<?> deleteBar(@RequestBody @Valid BarIdDto dto)
    {
        barService.deleteBar(dto.getBarId());
        return ResultUtil.success();
    }

    @PostMapping("/updateBar")
    public Result<?> updateBar(@RequestBody @Valid UpdateBarDto dto)
    {
        barService.updateBar(dto);
        return ResultUtil.success();
    }

    @PostMapping("/uploadBarImage")
    public Result<?> uploadBarImage(MultipartFile request, @RequestBody @Valid UploadBarImageDto dto)
    {
        try
        {
            return ResultUtil.success(barService.uploadBarImage(request.getInputStream(), dto.getBarId()));
        }
        catch (Exception e)
        {
            log.error("文件上传错误：{}", e.toString());
            return ResultUtil.error("上传图片错误");
        }
    }

    @PostMapping("/joinBar")
    public Result<?> joinBar(@RequestBody @Valid BarIdDto dto)
    {
        barService.joinBar(dto.getBarId());
        return ResultUtil.success();
    }

    @PostMapping("/leaveBar")
    public Result<?> leaveBar(@RequestBody @Valid BarIdDto dto)
    {
        barService.leaveBar(dto.getBarId());
        return ResultUtil.success();
    }

    @PostMapping("/kickMember")
    public Result<?> kickMember(@RequestBody @Valid BarMemberDto dto)
    {
        barService.kickMember(dto);
        return ResultUtil.success();
    }

    @PostMapping("/setAdmin")
    public Result<?> setAdmin(@RequestBody @Valid BarMemberDto dto)
    {
        barService.setAdmin(dto);
        return ResultUtil.success();
    }

    @PostMapping("/removeAdmin")
    public Result<?> removeAdmin(@RequestBody @Valid BarMemberDto dto)
    {
        barService.removeAdmin(dto);
        return ResultUtil.success();
    }

    @PostMapping("/muteMember")
    public Result<?> muteMember(@RequestBody @Valid BarMemberDto dto)
    {
        barService.muteMember(dto);
        return ResultUtil.success();
    }

    @PostMapping("/unmuteMember")
    public Result<?> unmuteMember(@RequestBody @Valid BarMemberDto dto)
    {
        barService.unmuteMember(dto);
        return ResultUtil.success();
    }

    @PostMapping("/setPostPermission")
    public Result<?> setPostPermission(@RequestBody @Valid SetPostPermissionDto dto)
    {
        barService.setPostPermission(dto);
        return ResultUtil.success();
    }

    @PostMapping("/setPostPin")
    public Result<?> setPostPin(@RequestBody @Valid SetPostPinDto dto)
    {
        barService.setPostPin(dto);
        return ResultUtil.success();
    }

    @PostMapping("/getBar")
    public Result<Bar> getBar(@RequestBody @Valid BarIdDto dto)
    {
        return ResultUtil.success(barService.getBar(dto.getBarId()));
    }

    @PostMapping("/getBarMembers")
    public Result<List<BarMember>> getBarMembers(@RequestBody @Valid BarIdDto dto)
    {
        return ResultUtil.success(barService.getBarMembers(dto.getBarId()));
    }

    @PostMapping("/checkPostPermission")
    public Result<Boolean> checkPostPermission(@RequestBody @Valid BarIdDto dto)
    {
        return ResultUtil.success(barService.checkPostPermission(dto.getBarId()));
    }
}
