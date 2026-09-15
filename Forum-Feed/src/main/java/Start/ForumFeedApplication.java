package Start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("Mapper")
@ComponentScan({"Entity","Controller","Service","Code","Config","Enums","Exception","Interceptor","Mapper","Result","Util"})
public class ForumFeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumFeedApplication.class, args);
    }

}