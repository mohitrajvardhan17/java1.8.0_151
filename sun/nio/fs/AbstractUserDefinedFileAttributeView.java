package sun.nio.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

abstract class AbstractUserDefinedFileAttributeView
  implements UserDefinedFileAttributeView, DynamicFileAttributeView
{
  protected AbstractUserDefinedFileAttributeView() {}
  
  protected void checkAccess(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    assert ((paramBoolean1) || (paramBoolean2));
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      if (paramBoolean1) {
        localSecurityManager.checkRead(paramString);
      }
      if (paramBoolean2) {
        localSecurityManager.checkWrite(paramString);
      }
      localSecurityManager.checkPermission(new RuntimePermission("accessUserDefinedAttributes"));
    }
  }
  
  public final String name()
  {
    return "user";
  }
  
  public final void setAttribute(String paramString, Object paramObject)
    throws IOException
  {
    ByteBuffer localByteBuffer;
    if ((paramObject instanceof byte[])) {
      localByteBuffer = ByteBuffer.wrap((byte[])paramObject);
    } else {
      localByteBuffer = (ByteBuffer)paramObject;
    }
    write(paramString, localByteBuffer);
  }
  
  public final Map<String, Object> readAttributes(String[] paramArrayOfString)
    throws IOException
  {
    Object localObject1 = new ArrayList();
    for (Object localObject3 : paramArrayOfString)
    {
      if (((String)localObject3).equals("*"))
      {
        localObject1 = list();
        break;
      }
      if (((String)localObject3).length() == 0) {
        throw new IllegalArgumentException();
      }
      ((List)localObject1).add(localObject3);
    }
    ??? = new HashMap();
    Iterator localIterator = ((List)localObject1).iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      int k = size(str);
      byte[] arrayOfByte1 = new byte[k];
      int m = read(str, ByteBuffer.wrap(arrayOfByte1));
      byte[] arrayOfByte2 = m == k ? arrayOfByte1 : Arrays.copyOf(arrayOfByte1, m);
      ((Map)???).put(str, arrayOfByte2);
    }
    return (Map<String, Object>)???;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\AbstractUserDefinedFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */