package javax.security.auth.callback;

import java.io.Serializable;

public class ConfirmationCallback
  implements Callback, Serializable
{
  private static final long serialVersionUID = -9095656433782481624L;
  public static final int UNSPECIFIED_OPTION = -1;
  public static final int YES_NO_OPTION = 0;
  public static final int YES_NO_CANCEL_OPTION = 1;
  public static final int OK_CANCEL_OPTION = 2;
  public static final int YES = 0;
  public static final int NO = 1;
  public static final int CANCEL = 2;
  public static final int OK = 3;
  public static final int INFORMATION = 0;
  public static final int WARNING = 1;
  public static final int ERROR = 2;
  private String prompt;
  private int messageType;
  private int optionType = -1;
  private int defaultOption;
  private String[] options;
  private int selection;
  
  public ConfirmationCallback(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt1 > 2) || (paramInt2 < 0) || (paramInt2 > 2)) {
      throw new IllegalArgumentException();
    }
    switch (paramInt2)
    {
    case 0: 
      if ((paramInt3 != 0) && (paramInt3 != 1)) {
        throw new IllegalArgumentException();
      }
      break;
    case 1: 
      if ((paramInt3 != 0) && (paramInt3 != 1) && (paramInt3 != 2)) {
        throw new IllegalArgumentException();
      }
      break;
    case 2: 
      if ((paramInt3 != 3) && (paramInt3 != 2)) {
        throw new IllegalArgumentException();
      }
      break;
    }
    messageType = paramInt1;
    optionType = paramInt2;
    defaultOption = paramInt3;
  }
  
  public ConfirmationCallback(int paramInt1, String[] paramArrayOfString, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > 2) || (paramArrayOfString == null) || (paramArrayOfString.length == 0) || (paramInt2 < 0) || (paramInt2 >= paramArrayOfString.length)) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if ((paramArrayOfString[i] == null) || (paramArrayOfString[i].length() == 0)) {
        throw new IllegalArgumentException();
      }
    }
    messageType = paramInt1;
    options = paramArrayOfString;
    defaultOption = paramInt2;
  }
  
  public ConfirmationCallback(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramInt1 < 0) || (paramInt1 > 2) || (paramInt2 < 0) || (paramInt2 > 2)) {
      throw new IllegalArgumentException();
    }
    switch (paramInt2)
    {
    case 0: 
      if ((paramInt3 != 0) && (paramInt3 != 1)) {
        throw new IllegalArgumentException();
      }
      break;
    case 1: 
      if ((paramInt3 != 0) && (paramInt3 != 1) && (paramInt3 != 2)) {
        throw new IllegalArgumentException();
      }
      break;
    case 2: 
      if ((paramInt3 != 3) && (paramInt3 != 2)) {
        throw new IllegalArgumentException();
      }
      break;
    }
    prompt = paramString;
    messageType = paramInt1;
    optionType = paramInt2;
    defaultOption = paramInt3;
  }
  
  public ConfirmationCallback(String paramString, int paramInt1, String[] paramArrayOfString, int paramInt2)
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramInt1 < 0) || (paramInt1 > 2) || (paramArrayOfString == null) || (paramArrayOfString.length == 0) || (paramInt2 < 0) || (paramInt2 >= paramArrayOfString.length)) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if ((paramArrayOfString[i] == null) || (paramArrayOfString[i].length() == 0)) {
        throw new IllegalArgumentException();
      }
    }
    prompt = paramString;
    messageType = paramInt1;
    options = paramArrayOfString;
    defaultOption = paramInt2;
  }
  
  public String getPrompt()
  {
    return prompt;
  }
  
  public int getMessageType()
  {
    return messageType;
  }
  
  public int getOptionType()
  {
    return optionType;
  }
  
  public String[] getOptions()
  {
    return options;
  }
  
  public int getDefaultOption()
  {
    return defaultOption;
  }
  
  public void setSelectedIndex(int paramInt)
  {
    selection = paramInt;
  }
  
  public int getSelectedIndex()
  {
    return selection;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\callback\ConfirmationCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */