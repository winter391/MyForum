package Start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@MapperScan("Mapper")
@ComponentScan(basePackages = {"Code","Controller","DTO", "Exception","Service","Start","Util","V0"})
public class start
{
    public static void main(String[] args)
    {
        SpringApplication.run(start.class);
    }
}
