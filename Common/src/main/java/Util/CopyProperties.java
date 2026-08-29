package Util;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

public class CopyProperties
{

    public static <T> T copyProperties(Object source,Class<T> target)
    {
        try
        {
            T Result = target.getConstructor().newInstance();
            copyProperties(source,Result);
            return Result;
        }
        catch(NoSuchMethodException e)
        {
            return null;
        }
        catch (Exception e)
        {
            ReflectionUtils.handleReflectionException(e);
            return null;
        }
    }


    public static void copyProperties(Object source,Object target)
    {
        try
        {
            BeanUtils.copyProperties(source,target);
        }
        catch (Exception e)
        {
            ReflectionUtils.handleReflectionException(e);
        }
    }
}
