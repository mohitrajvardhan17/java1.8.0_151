package sun.corba;

import com.sun.corba.se.impl.io.ValueUtility;
import sun.misc.Unsafe;

public class SharedSecrets
{
  private static final Unsafe unsafe = ;
  private static JavaCorbaAccess javaCorbaAccess;
  
  public SharedSecrets() {}
  
  public static JavaCorbaAccess getJavaCorbaAccess()
  {
    if (javaCorbaAccess == null) {
      unsafe.ensureClassInitialized(ValueUtility.class);
    }
    return javaCorbaAccess;
  }
  
  public static void setJavaCorbaAccess(JavaCorbaAccess paramJavaCorbaAccess)
  {
    javaCorbaAccess = paramJavaCorbaAccess;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\corba\SharedSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */