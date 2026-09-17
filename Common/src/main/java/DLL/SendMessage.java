package DLL;


import java.util.List;

public interface SendMessage
{
    public <T>void SendMessage(List<Integer> serverId, T message, String messageType);
}
