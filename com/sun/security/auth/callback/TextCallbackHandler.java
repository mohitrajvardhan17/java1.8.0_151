package com.sun.security.auth.callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import jdk.Exported;
import sun.security.util.Password;

@Exported
public class TextCallbackHandler
  implements CallbackHandler
{
  public TextCallbackHandler() {}
  
  public void handle(Callback[] paramArrayOfCallback)
    throws IOException, UnsupportedCallbackException
  {
    ConfirmationCallback localConfirmationCallback = null;
    for (int i = 0; i < paramArrayOfCallback.length; i++)
    {
      Object localObject;
      String str1;
      if ((paramArrayOfCallback[i] instanceof TextOutputCallback))
      {
        localObject = (TextOutputCallback)paramArrayOfCallback[i];
        switch (((TextOutputCallback)localObject).getMessageType())
        {
        case 0: 
          str1 = "";
          break;
        case 1: 
          str1 = "Warning: ";
          break;
        case 2: 
          str1 = "Error: ";
          break;
        default: 
          throw new UnsupportedCallbackException(paramArrayOfCallback[i], "Unrecognized message type");
        }
        String str2 = ((TextOutputCallback)localObject).getMessage();
        if (str2 != null) {
          str1 = str1 + str2;
        }
        if (str1 != null) {
          System.err.println(str1);
        }
      }
      else if ((paramArrayOfCallback[i] instanceof NameCallback))
      {
        localObject = (NameCallback)paramArrayOfCallback[i];
        if (((NameCallback)localObject).getDefaultName() == null) {
          System.err.print(((NameCallback)localObject).getPrompt());
        } else {
          System.err.print(((NameCallback)localObject).getPrompt() + " [" + ((NameCallback)localObject).getDefaultName() + "] ");
        }
        System.err.flush();
        str1 = readLine();
        if (str1.equals("")) {
          str1 = ((NameCallback)localObject).getDefaultName();
        }
        ((NameCallback)localObject).setName(str1);
      }
      else if ((paramArrayOfCallback[i] instanceof PasswordCallback))
      {
        localObject = (PasswordCallback)paramArrayOfCallback[i];
        System.err.print(((PasswordCallback)localObject).getPrompt());
        System.err.flush();
        ((PasswordCallback)localObject).setPassword(Password.readPassword(System.in, ((PasswordCallback)localObject).isEchoOn()));
      }
      else if ((paramArrayOfCallback[i] instanceof ConfirmationCallback))
      {
        localConfirmationCallback = (ConfirmationCallback)paramArrayOfCallback[i];
      }
      else
      {
        throw new UnsupportedCallbackException(paramArrayOfCallback[i], "Unrecognized Callback");
      }
    }
    if (localConfirmationCallback != null) {
      doConfirmation(localConfirmationCallback);
    }
  }
  
  private String readLine()
    throws IOException
  {
    String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
    if (str == null) {
      throw new IOException("Cannot read from System.in");
    }
    return str;
  }
  
  private void doConfirmation(ConfirmationCallback paramConfirmationCallback)
    throws IOException, UnsupportedCallbackException
  {
    int i = paramConfirmationCallback.getMessageType();
    String str1;
    switch (i)
    {
    case 1: 
      str1 = "Warning: ";
      break;
    case 2: 
      str1 = "Error: ";
      break;
    case 0: 
      str1 = "";
      break;
    default: 
      throw new UnsupportedCallbackException(paramConfirmationCallback, "Unrecognized message type: " + i);
    }
    int j = paramConfirmationCallback.getOptionType();
    Object arrayOf1OptionInfo;
    switch (j)
    {
    case 0: 
      arrayOf1OptionInfo = new 1OptionInfo[] { new Object()new Object
      {
        String name;
        int value;
      }, new Object()
      {
        String name;
        int value;
      } };
      break;
    case 1: 
      arrayOf1OptionInfo = new 1OptionInfo[] { new Object()new Object
      {
        String name;
        int value;
      }, new Object()new Object
      {
        String name;
        int value;
      }, new Object()
      {
        String name;
        int value;
      } };
      break;
    case 2: 
      arrayOf1OptionInfo = new 1OptionInfo[] { new Object()new Object
      {
        String name;
        int value;
      }, new Object()
      {
        String name;
        int value;
      } };
      break;
    case -1: 
      String[] arrayOfString = paramConfirmationCallback.getOptions();
      arrayOf1OptionInfo = new 1OptionInfo[arrayOfString.length];
      for (int m = 0; m < arrayOf1OptionInfo.length; m++) {
        arrayOf1OptionInfo[m = new Object()
        {
          String name;
          int value;
        };
      }
      break;
    default: 
      throw new UnsupportedCallbackException(paramConfirmationCallback, "Unrecognized option type: " + j);
    }
    int k = paramConfirmationCallback.getDefaultOption();
    String str2 = paramConfirmationCallback.getPrompt();
    if (str2 == null) {
      str2 = "";
    }
    str2 = str1 + str2;
    if (!str2.equals("")) {
      System.err.println(str2);
    }
    for (int n = 0; n < arrayOf1OptionInfo.length; n++) {
      if (j == -1) {
        System.err.println(n + ". " + name + (n == k ? " [default]" : ""));
      } else {
        System.err.println(n + ". " + name + (value == k ? " [default]" : ""));
      }
    }
    System.err.print("Enter a number: ");
    System.err.flush();
    try
    {
      n = Integer.parseInt(readLine());
      if ((n < 0) || (n > arrayOf1OptionInfo.length - 1)) {
        n = k;
      }
      n = value;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      n = k;
    }
    paramConfirmationCallback.setSelectedIndex(n);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\callback\TextCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */