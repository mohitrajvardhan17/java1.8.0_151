package com.sun.corba.se.impl.naming.cosnaming;

import java.io.StringWriter;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

public class InterOperableNamingImpl
{
  public InterOperableNamingImpl() {}
  
  public String convertToString(NameComponent[] paramArrayOfNameComponent)
  {
    String str1 = convertNameComponentToString(paramArrayOfNameComponent[0]);
    for (int i = 1; i < paramArrayOfNameComponent.length; i++)
    {
      String str2 = convertNameComponentToString(paramArrayOfNameComponent[i]);
      if (str2 != null) {
        str1 = str1 + "/" + convertNameComponentToString(paramArrayOfNameComponent[i]);
      }
    }
    return str1;
  }
  
  private String convertNameComponentToString(NameComponent paramNameComponent)
  {
    if (((id == null) || (id.length() == 0)) && ((kind == null) || (kind.length() == 0))) {
      return ".";
    }
    if ((id == null) || (id.length() == 0))
    {
      str1 = addEscape(kind);
      return "." + str1;
    }
    if ((kind == null) || (kind.length() == 0))
    {
      str1 = addEscape(id);
      return str1;
    }
    String str1 = addEscape(id);
    String str2 = addEscape(kind);
    return str1 + "." + str2;
  }
  
  private String addEscape(String paramString)
  {
    StringBuffer localStringBuffer;
    if ((paramString != null) && ((paramString.indexOf('.') != -1) || (paramString.indexOf('/') != -1)))
    {
      localStringBuffer = new StringBuffer();
      for (int i = 0; i < paramString.length(); i++)
      {
        char c = paramString.charAt(i);
        if ((c != '.') && (c != '/'))
        {
          localStringBuffer.append(c);
        }
        else
        {
          localStringBuffer.append('\\');
          localStringBuffer.append(c);
        }
      }
    }
    else
    {
      return paramString;
    }
    return new String(localStringBuffer);
  }
  
  public NameComponent[] convertToNameComponent(String paramString)
    throws InvalidName
  {
    String[] arrayOfString = breakStringToNameComponents(paramString);
    if ((arrayOfString == null) || (arrayOfString.length == 0)) {
      return null;
    }
    NameComponent[] arrayOfNameComponent = new NameComponent[arrayOfString.length];
    for (int i = 0; i < arrayOfString.length; i++) {
      arrayOfNameComponent[i] = createNameComponentFromString(arrayOfString[i]);
    }
    return arrayOfNameComponent;
  }
  
  private String[] breakStringToNameComponents(String paramString)
  {
    int[] arrayOfInt = new int[100];
    int i = 0;
    int j = 0;
    while (j <= paramString.length())
    {
      arrayOfInt[i] = paramString.indexOf(47, j);
      if (arrayOfInt[i] == -1)
      {
        j = paramString.length() + 1;
      }
      else if ((arrayOfInt[i] > 0) && (paramString.charAt(arrayOfInt[i] - 1) == '\\'))
      {
        j = arrayOfInt[i] + 1;
        arrayOfInt[i] = -1;
      }
      else
      {
        j = arrayOfInt[i] + 1;
        i++;
      }
    }
    if (i == 0)
    {
      String[] arrayOfString = new String[1];
      arrayOfString[0] = paramString;
      return arrayOfString;
    }
    if (i != 0) {
      i++;
    }
    return StringComponentsFromIndices(arrayOfInt, i, paramString);
  }
  
  private String[] StringComponentsFromIndices(int[] paramArrayOfInt, int paramInt, String paramString)
  {
    String[] arrayOfString = new String[paramInt];
    int i = 0;
    int j = paramArrayOfInt[0];
    for (int k = 0; k < paramInt; k++)
    {
      arrayOfString[k] = paramString.substring(i, j);
      if ((paramArrayOfInt[k] < paramString.length() - 1) && (paramArrayOfInt[k] != -1))
      {
        i = paramArrayOfInt[k] + 1;
      }
      else
      {
        i = 0;
        k = paramInt;
      }
      if ((k + 1 < paramArrayOfInt.length) && (paramArrayOfInt[(k + 1)] < paramString.length() - 1) && (paramArrayOfInt[(k + 1)] != -1)) {
        j = paramArrayOfInt[(k + 1)];
      } else {
        k = paramInt;
      }
      if ((i != 0) && (k == paramInt)) {
        arrayOfString[(paramInt - 1)] = paramString.substring(i);
      }
    }
    return arrayOfString;
  }
  
  private NameComponent createNameComponentFromString(String paramString)
    throws InvalidName
  {
    String str1 = null;
    String str2 = null;
    if ((paramString == null) || (paramString.length() == 0) || (paramString.endsWith("."))) {
      throw new InvalidName();
    }
    int i = paramString.indexOf('.', 0);
    if (i == -1)
    {
      str1 = paramString;
    }
    else if (i == 0)
    {
      if (paramString.length() != 1) {
        str2 = paramString.substring(1);
      }
    }
    else if (paramString.charAt(i - 1) != '\\')
    {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    }
    else
    {
      int j = 0;
      while ((i < paramString.length()) && (j != 1))
      {
        i = paramString.indexOf('.', i + 1);
        if (i > 0)
        {
          if (paramString.charAt(i - 1) != '\\') {
            j = 1;
          }
        }
        else {
          i = paramString.length();
        }
      }
      if (j == 1)
      {
        str1 = paramString.substring(0, i);
        str2 = paramString.substring(i + 1);
      }
      else
      {
        str1 = paramString;
      }
    }
    str1 = cleanEscapeCharacter(str1);
    str2 = cleanEscapeCharacter(str2);
    if (str1 == null) {
      str1 = "";
    }
    if (str2 == null) {
      str2 = "";
    }
    return new NameComponent(str1, str2);
  }
  
  private String cleanEscapeCharacter(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    int i = paramString.indexOf('\\');
    if (i == 0) {
      return paramString;
    }
    StringBuffer localStringBuffer1 = new StringBuffer(paramString);
    StringBuffer localStringBuffer2 = new StringBuffer();
    for (int j = 0; j < paramString.length(); j++)
    {
      char c1 = localStringBuffer1.charAt(j);
      if (c1 != '\\')
      {
        localStringBuffer2.append(c1);
      }
      else if (j + 1 < paramString.length())
      {
        char c2 = localStringBuffer1.charAt(j + 1);
        if (Character.isLetterOrDigit(c2)) {
          localStringBuffer2.append(c1);
        }
      }
    }
    return new String(localStringBuffer2);
  }
  
  public String createURLBasedAddress(String paramString1, String paramString2)
    throws InvalidAddress
  {
    String str = null;
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      throw new InvalidAddress();
    }
    str = "corbaname:" + paramString1 + "#" + encode(paramString2);
    return str;
  }
  
  private String encode(String paramString)
  {
    StringWriter localStringWriter = new StringWriter();
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      char c = paramString.charAt(j);
      if (Character.isLetterOrDigit(c))
      {
        localStringWriter.write(c);
      }
      else if ((c == ';') || (c == '/') || (c == '?') || (c == ':') || (c == '@') || (c == '&') || (c == '=') || (c == '+') || (c == '$') || (c == ';') || (c == '-') || (c == '_') || (c == '.') || (c == '!') || (c == '~') || (c == '*') || (c == ' ') || (c == '(') || (c == ')'))
      {
        localStringWriter.write(c);
      }
      else
      {
        localStringWriter.write(37);
        String str = Integer.toHexString(c);
        localStringWriter.write(str);
      }
    }
    return localStringWriter.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\InterOperableNamingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */