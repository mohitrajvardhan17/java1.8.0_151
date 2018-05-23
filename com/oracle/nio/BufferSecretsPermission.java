package com.oracle.nio;

import java.security.BasicPermission;

public final class BufferSecretsPermission
  extends BasicPermission
{
  private static final long serialVersionUID = 0L;
  
  public BufferSecretsPermission(String paramString)
  {
    super(paramString);
    if (!paramString.equals("access")) {
      throw new IllegalArgumentException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\nio\BufferSecretsPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */