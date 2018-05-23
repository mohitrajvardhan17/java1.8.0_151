package sun.nio.ch.sctp;

import com.sun.nio.sctp.SctpSocketOption;

public class SctpStdSocketOption<T>
  implements SctpSocketOption<T>
{
  public static final int SCTP_DISABLE_FRAGMENTS = 1;
  public static final int SCTP_EXPLICIT_COMPLETE = 2;
  public static final int SCTP_FRAGMENT_INTERLEAVE = 3;
  public static final int SCTP_NODELAY = 4;
  public static final int SO_SNDBUF = 5;
  public static final int SO_RCVBUF = 6;
  public static final int SO_LINGER = 7;
  private final String name;
  private final Class<T> type;
  private int constValue;
  
  public SctpStdSocketOption(String paramString, Class<T> paramClass)
  {
    name = paramString;
    type = paramClass;
  }
  
  public SctpStdSocketOption(String paramString, Class<T> paramClass, int paramInt)
  {
    name = paramString;
    type = paramClass;
    constValue = paramInt;
  }
  
  public String name()
  {
    return name;
  }
  
  public Class<T> type()
  {
    return type;
  }
  
  public String toString()
  {
    return name;
  }
  
  int constValue()
  {
    return constValue;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\sctp\SctpStdSocketOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */