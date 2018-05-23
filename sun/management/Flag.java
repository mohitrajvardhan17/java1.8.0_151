package sun.management;

import com.sun.management.VMOption;
import com.sun.management.VMOption.Origin;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

class Flag
{
  private String name;
  private Object value;
  private VMOption.Origin origin;
  private boolean writeable;
  private boolean external;
  
  Flag(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, VMOption.Origin paramOrigin)
  {
    name = paramString;
    value = (paramObject == null ? "" : paramObject);
    origin = paramOrigin;
    writeable = paramBoolean1;
    external = paramBoolean2;
  }
  
  Object getValue()
  {
    return value;
  }
  
  boolean isWriteable()
  {
    return writeable;
  }
  
  boolean isExternal()
  {
    return external;
  }
  
  VMOption getVMOption()
  {
    return new VMOption(name, value.toString(), writeable, origin);
  }
  
  static Flag getFlag(String paramString)
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = paramString;
    List localList = getFlags(arrayOfString, 1);
    if (localList.isEmpty()) {
      return null;
    }
    return (Flag)localList.get(0);
  }
  
  static List<Flag> getAllFlags()
  {
    int i = getInternalFlagCount();
    return getFlags(null, i);
  }
  
  private static List<Flag> getFlags(String[] paramArrayOfString, int paramInt)
  {
    Flag[] arrayOfFlag1 = new Flag[paramInt];
    int i = getFlags(paramArrayOfString, arrayOfFlag1, paramInt);
    ArrayList localArrayList = new ArrayList();
    for (Flag localFlag : arrayOfFlag1) {
      if (localFlag != null) {
        localArrayList.add(localFlag);
      }
    }
    return localArrayList;
  }
  
  private static native String[] getAllFlagNames();
  
  private static native int getFlags(String[] paramArrayOfString, Flag[] paramArrayOfFlag, int paramInt);
  
  private static native int getInternalFlagCount();
  
  static synchronized native void setLongValue(String paramString, long paramLong);
  
  static synchronized native void setBooleanValue(String paramString, boolean paramBoolean);
  
  static synchronized native void setStringValue(String paramString1, String paramString2);
  
  private static native void initialize();
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("management");
        return null;
      }
    });
    initialize();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\Flag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */