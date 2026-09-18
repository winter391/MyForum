package Start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("Mapper")
@ComponentScan({"Entity","DLL","Controller","Service","Code","Config","Enums","Exception","Interceptor","Mapper","Result","Util","Start","Listener"})
public class ForumNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumNotificationApplication.class, args);
    }

}