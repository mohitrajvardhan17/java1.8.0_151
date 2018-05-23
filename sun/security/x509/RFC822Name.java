package sun.security.x509;

import java.io.IOException;
import java.util.Locale;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class RFC822Name
  implements GeneralNameInterface
{
  private String name;
  
  public RFC822Name(DerValue paramDerValue)
    throws IOException
  {
    name = paramDerValue.getIA5String();
    parseName(name);
  }
  
  public RFC822Name(String paramString)
    throws IOException
  {
    parseName(paramString);
    name = paramString;
  }
  
  public void parseName(String paramString)
    throws IOException
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IOException("RFC822Name may not be null or empty");
    }
    String str = paramString.substring(paramString.indexOf('@') + 1);
    if (str.length() == 0) {
      throw new IOException("RFC822Name may not end with @");
    }
    if ((str.startsWith(".")) && (str.length() == 1)) {
      throw new IOException("RFC822Name domain may not be just .");
    }
  }
  
  public int getType()
  {
    return 1;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    paramDerOutputStream.putIA5String(name);
  }
  
  public String toString()
  {
    return "RFC822Name: " + name;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof RFC822Name)) {
      return false;
    }
    RFC822Name localRFC822Name = (RFC822Name)paramObject;
    return name.equalsIgnoreCase(name);
  }
  
  public int hashCode()
  {
    return name.toUpperCase(Locale.ENGLISH).hashCode();
  }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface)
    throws UnsupportedOperationException
  {
    int i;
    if (paramGeneralNameInterface == null)
    {
      i = -1;
    }
    else if (paramGeneralNameInterface.getType() != 1)
    {
      i = -1;
    }
    else
    {
      String str1 = ((RFC822Name)paramGeneralNameInterface).getName().toLowerCase(Locale.ENGLISH);
      String str2 = name.toLowerCase(Locale.ENGLISH);
      if (str1.equals(str2))
      {
        i = 0;
      }
      else
      {
        int j;
        if (str2.endsWith(str1))
        {
          if (str1.indexOf('@') != -1)
          {
            i = 3;
          }
          else if (str1.startsWith("."))
          {
            i = 2;
          }
          else
          {
            j = str2.lastIndexOf(str1);
            if (str2.charAt(j - 1) == '@') {
              i = 2;
            } else {
              i = 3;
            }
          }
        }
        else if (str1.endsWith(str2))
        {
          if (str2.indexOf('@') != -1)
          {
            i = 3;
          }
          else if (str2.startsWith("."))
          {
            i = 1;
          }
          else
          {
            j = str1.lastIndexOf(str2);
            if (str1.charAt(j - 1) == '@') {
              i = 1;
            } else {
              i = 3;
            }
          }
        }
        else {
          i = 3;
        }
      }
    }
    return i;
  }
  
  public int subtreeDepth()
    throws UnsupportedOperationException
  {
    String str = name;
    int i = 1;
    int j = str.lastIndexOf('@');
    if (j >= 0)
    {
      i++;
      str = str.substring(j + 1);
    }
    while (str.lastIndexOf('.') >= 0)
    {
      str = str.substring(0, str.lastIndexOf('.'));
      i++;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\RFC822Name.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */