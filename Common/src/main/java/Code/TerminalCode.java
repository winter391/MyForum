package Code;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TerminalCode
{
    public static Integer PC_TERMINAL_CODE =0;

    public static Integer MOBILE_TERMINAL_CODE =1;

    public static List<Integer> getAllTerminal()
    {
        return List.of(PC_TERMINAL_CODE,MOBILE_TERMINAL_CODE);
    }
}
