package Service.Impl;

import Entity.Post;
import Mapper.PostMapper;
import Service.PostService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class PostServiceImpl extends ServiceImpl<PostMapper,Post> implements PostService
{

}
