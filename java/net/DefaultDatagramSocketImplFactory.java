package java.net;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import sun.security.action.GetPropertyAction;

class DefaultDatagramSocketImplFactory
{
  private static final Class<?> prefixImplClass;
  private static float version;
  private static boolean preferIPv4Stack = false;
  private static final boolean useDualStackImpl;
  private static String exclBindProp;
  private static final boolean exclusiveBind;
  
  DefaultDatagramSocketImplFactory() {}
  
  static DatagramSocketImpl createDatagramSocketImpl(boolean paramBoolean)
    throws SocketException
  {
    if (prefixImplClass != null) {
      try
      {
        return (DatagramSocketImpl)prefixImplClass.newInstance();
      }
      catch (Exception localException)
      {
        throw new SocketException("can't instantiate DatagramSocketImpl");
      }
    }
    if ((useDualStackImpl) && (!paramBoolean)) {
      return new DualStackPlainDatagramSocketImpl(exclusiveBind);
    }
    return new TwoStacksPlainDatagramSocketImpl((exclusiveBind) && (!paramBoolean));
  }
  
  static
  {
    Class localClass = null;
    boolean bool1 = false;
    boolean bool2 = true;
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        DefaultDatagramSocketImplFactory.access$002(0.0F);
        try
        {
          DefaultDatagramSocketImplFactory.access$002(Float.parseFloat(System.getProperties().getProperty("os.version")));
          DefaultDatagramSocketImplFactory.access$102(Boolean.parseBoolean(System.getProperties().getProperty("java.net.preferIPv4Stack")));
          DefaultDatagramSocketImplFactory.access$202(System.getProperty("sun.net.useExclusiveBind"));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError(localNumberFormatException);
          }
        }
        return null;
      }
    });
    if ((version >= 6.0D) && (!preferIPv4Stack)) {
      bool1 = true;
    }
    if (exclBindProp != null) {
      bool2 = exclBindProp.length() == 0 ? true : Boolean.parseBoolean(exclBindProp);
    } else if (version < 6.0D) {
      bool2 = false;
    }
    String str = null;
    try
    {
      str = (String)AccessController.doPrivileged(new GetPropertyAction("impl.prefix", null));
      if (str != null) {
        localClass = Class.forName("java.net." + str + "DatagramSocketImpl");
      }
    }
    catch (Exception localException)
    {
      System.err.println("Can't find class: java.net." + str + "DatagramSocketImpl: check impl.prefix property");
    }
    prefixImplClass = localClass;
    useDualStackImpl = bool1;
    exclusiveBind = bool2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\DefaultDatagramSocketImplFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */