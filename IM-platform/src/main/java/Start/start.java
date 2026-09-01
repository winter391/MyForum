package Start;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("Mapper")
@ComponentScan({"Code","Config","Controller","DLL","Dto","Entity","Service","Mapper","Exception","Interceptor","Util"})
public class start
{
    public static void main(String[] args) {
        SpringApplication.run(start.class, args);
    }
}
