package java.awt.datatransfer;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Locale;

class MimeType
  implements Externalizable, Cloneable
{
  static final long serialVersionUID = -6568722458793895906L;
  private String primaryType;
  private String subType;
  private MimeTypeParameterList parameters;
  private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";
  
  public MimeType() {}
  
  public MimeType(String paramString)
    throws MimeTypeParseException
  {
    parse(paramString);
  }
  
  public MimeType(String paramString1, String paramString2)
    throws MimeTypeParseException
  {
    this(paramString1, paramString2, new MimeTypeParameterList());
  }
  
  public MimeType(String paramString1, String paramString2, MimeTypeParameterList paramMimeTypeParameterList)
    throws MimeTypeParseException
  {
    if (isValidToken(paramString1)) {
      primaryType = paramString1.toLowerCase(Locale.ENGLISH);
    } else {
      throw new MimeTypeParseException("Primary type is invalid.");
    }
    if (isValidToken(paramString2)) {
      subType = paramString2.toLowerCase(Locale.ENGLISH);
    } else {
      throw new MimeTypeParseException("Sub type is invalid.");
    }
    parameters = ((MimeTypeParameterList)paramMimeTypeParameterList.clone());
  }
  
  public int hashCode()
  {
    int i = 0;
    i += primaryType.hashCode();
    i += subType.hashCode();
    i += parameters.hashCode();
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof MimeType)) {
      return false;
    }
    MimeType localMimeType = (MimeType)paramObject;
    boolean bool = (primaryType.equals(primaryType)) && (subType.equals(subType)) && (parameters.equals(parameters));
    return bool;
  }
  
  private void parse(String paramString)
    throws MimeTypeParseException
  {
    int i = paramString.indexOf('/');
    int j = paramString.indexOf(';');
    if ((i < 0) && (j < 0)) {
      throw new MimeTypeParseException("Unable to find a sub type.");
    }
    if ((i < 0) && (j >= 0)) {
      throw new MimeTypeParseException("Unable to find a sub type.");
    }
    if ((i >= 0) && (j < 0))
    {
      primaryType = paramString.substring(0, i).trim().toLowerCase(Locale.ENGLISH);
      subType = paramString.substring(i + 1).trim().toLowerCase(Locale.ENGLISH);
      parameters = new MimeTypeParameterList();
    }
    else if (i < j)
    {
      primaryType = paramString.substring(0, i).trim().toLowerCase(Locale.ENGLISH);
      subType = paramString.substring(i + 1, j).trim().toLowerCase(Locale.ENGLISH);
      parameters = new MimeTypeParameterList(paramString.substring(j));
    }
    else
    {
      throw new MimeTypeParseException("Unable to find a sub type.");
    }
    if (!isValidToken(primaryType)) {
      throw new MimeTypeParseException("Primary type is invalid.");
    }
    if (!isValidToken(subType)) {
      throw new MimeTypeParseException("Sub type is invalid.");
    }
  }
  
  public String getPrimaryType()
  {
    return primaryType;
  }
  
  public String getSubType()
  {
    return subType;
  }
  
  public MimeTypeParameterList getParameters()
  {
    return (MimeTypeParameterList)parameters.clone();
  }
  
  public String getParameter(String paramString)
  {
    return parameters.get(paramString);
  }
  
  public void setParameter(String paramString1, String paramString2)
  {
    parameters.set(paramString1, paramString2);
  }
  
  public void removeParameter(String paramString)
  {
    parameters.remove(paramString);
  }
  
  public String toString()
  {
    return getBaseType() + parameters.toString();
  }
  
  public String getBaseType()
  {
    return primaryType + "/" + subType;
  }
  
  public boolean match(MimeType paramMimeType)
  {
    if (paramMimeType == null) {
      return false;
    }
    return (primaryType.equals(paramMimeType.getPrimaryType())) && ((subType.equals("*")) || (paramMimeType.getSubType().equals("*")) || (subType.equals(paramMimeType.getSubType())));
  }
  
  public boolean match(String paramString)
    throws MimeTypeParseException
  {
    if (paramString == null) {
      return false;
    }
    return match(new MimeType(paramString));
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    String str = toString();
    if (str.length() <= 65535)
    {
      paramObjectOutput.writeUTF(str);
    }
    else
    {
      paramObjectOutput.writeByte(0);
      paramObjectOutput.writeByte(0);
      paramObjectOutput.writeInt(str.length());
      paramObjectOutput.write(str.getBytes());
    }
  }
  
  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    String str = paramObjectInput.readUTF();
    if ((str == null) || (str.length() == 0))
    {
      byte[] arrayOfByte = new byte[paramObjectInput.readInt()];
      paramObjectInput.readFully(arrayOfByte);
      str = new String(arrayOfByte);
    }
    try
    {
      parse(str);
    }
    catch (MimeTypeParseException localMimeTypeParseException)
    {
      throw new IOException(localMimeTypeParseException.toString());
    }
  }
  
  public Object clone()
  {
    MimeType localMimeType = null;
    try
    {
      localMimeType = (MimeType)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    parameters = ((MimeTypeParameterList)parameters.clone());
    return localMimeType;
  }
  
  private static boolean isTokenChar(char paramChar)
  {
    return (paramChar > ' ') && (paramChar < '') && ("()<>@,;:\\\"/[]?=".indexOf(paramChar) < 0);
  }
  
  private boolean isValidToken(String paramString)
  {
    int i = paramString.length();
    if (i > 0)
    {
      for (int j = 0; j < i; j++)
      {
        char c = paramString.charAt(j);
        if (!isTokenChar(c)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\datatransfer\MimeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */