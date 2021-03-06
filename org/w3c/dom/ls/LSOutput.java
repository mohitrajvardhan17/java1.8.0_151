package org.w3c.dom.ls;

import java.io.OutputStream;
import java.io.Writer;

public abstract interface LSOutput
{
  public abstract Writer getCharacterStream();
  
  public abstract void setCharacterStream(Writer paramWriter);
  
  public abstract OutputStream getByteStream();
  
  public abstract void setByteStream(OutputStream paramOutputStream);
  
  public abstract String getSystemId();
  
  public abstract void setSystemId(String paramString);
  
  public abstract String getEncoding();
  
  public abstract void setEncoding(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\ls\LSOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */