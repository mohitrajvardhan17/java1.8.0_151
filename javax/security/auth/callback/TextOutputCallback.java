package javax.security.auth.callback;

import java.io.Serializable;

public class TextOutputCallback
  implements Callback, Serializable
{
  private static final long serialVersionUID = 1689502495511663102L;
  public static final int INFORMATION = 0;
  public static final int WARNING = 1;
  public static final int ERROR = 2;
  private int messageType;
  private String message;
  
  public TextOutputCallback(int paramInt, String paramString)
  {
    if (((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) || (paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException();
    }
    messageType = paramInt;
    message = paramString;
  }
  
  public int getMessageType()
  {
    return messageType;
  }
  
  public String getMessage()
  {
    return message;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\callback\TextOutputCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */