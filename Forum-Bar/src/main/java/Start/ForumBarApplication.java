package Start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"Entity","Controller","Service","Code","Config","Enums","Exception","Interceptor","Mapper","Result","Util"})
@MapperScan("Mapper")
public class ForumBarApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumBarApplication.class, args);
    }

}
