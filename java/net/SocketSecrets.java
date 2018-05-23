package java.net;

import java.io.IOException;

class SocketSecrets
{
  SocketSecrets() {}
  
  private static <T> void setOption(Object paramObject, SocketOption<T> paramSocketOption, T paramT)
    throws IOException
  {
    SocketImpl localSocketImpl;
    if ((paramObject instanceof Socket)) {
      localSocketImpl = ((Socket)paramObject).getImpl();
    } else if ((paramObject instanceof ServerSocket)) {
      localSocketImpl = ((ServerSocket)paramObject).getImpl();
    } else {
      throw new IllegalArgumentException();
    }
    localSocketImpl.setOption(paramSocketOption, paramT);
  }
  
  private static <T> T getOption(Object paramObject, SocketOption<T> paramSocketOption)
    throws IOException
  {
    SocketImpl localSocketImpl;
    if ((paramObject instanceof Socket)) {
      localSocketImpl = ((Socket)paramObject).getImpl();
    } else if ((paramObject instanceof ServerSocket)) {
      localSocketImpl = ((ServerSocket)paramObject).getImpl();
    } else {
      throw new IllegalArgumentException();
    }
    return (T)localSocketImpl.getOption(paramSocketOption);
  }
  
  private static <T> void setOption(DatagramSocket paramDatagramSocket, SocketOption<T> paramSocketOption, T paramT)
    throws IOException
  {
    paramDatagramSocket.getImpl().setOption(paramSocketOption, paramT);
  }
  
  private static <T> T getOption(DatagramSocket paramDatagramSocket, SocketOption<T> paramSocketOption)
    throws IOException
  {
    return (T)paramDatagramSocket.getImpl().getOption(paramSocketOption);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\SocketSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */