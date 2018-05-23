package javax.xml.transform;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Objects;

public class TransformerException
  extends Exception
{
  private static final long serialVersionUID = 975798773772956428L;
  SourceLocator locator;
  Throwable containedException;
  
  public SourceLocator getLocator()
  {
    return locator;
  }
  
  public void setLocator(SourceLocator paramSourceLocator)
  {
    locator = paramSourceLocator;
  }
  
  public Throwable getException()
  {
    return containedException;
  }
  
  public Throwable getCause()
  {
    return containedException == this ? null : containedException;
  }
  
  public synchronized Throwable initCause(Throwable paramThrowable)
  {
    if (containedException != null) {
      throw new IllegalStateException("Can't overwrite cause");
    }
    if (paramThrowable == this) {
      throw new IllegalArgumentException("Self-causation not permitted");
    }
    containedException = paramThrowable;
    return this;
  }
  
  public TransformerException(String paramString)
  {
    this(paramString, null, null);
  }
  
  public TransformerException(Throwable paramThrowable)
  {
    this(null, null, paramThrowable);
  }
  
  public TransformerException(String paramString, Throwable paramThrowable)
  {
    this(paramString, null, paramThrowable);
  }
  
  public TransformerException(String paramString, SourceLocator paramSourceLocator)
  {
    this(paramString, paramSourceLocator, null);
  }
  
  public TransformerException(String paramString, SourceLocator paramSourceLocator, Throwable paramThrowable)
  {
    super((paramString == null) || (paramString.length() == 0) ? paramThrowable.toString() : paramThrowable == null ? "" : paramString);
    containedException = paramThrowable;
    locator = paramSourceLocator;
  }
  
  public String getMessageAndLocation()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(Objects.toString(super.getMessage(), ""));
    localStringBuilder.append(Objects.toString(getLocationAsString(), ""));
    return localStringBuilder.toString();
  }
  
  public String getLocationAsString()
  {
    if (locator == null) {
      return null;
    }
    if (System.getSecurityManager() == null) {
      return getLocationString();
    }
    (String)AccessController.doPrivileged(new PrivilegedAction()new AccessControlContext
    {
      public String run()
      {
        return TransformerException.this.getLocationString();
      }
    }, new AccessControlContext(new ProtectionDomain[] { getNonPrivDomain() }));
  }
  
  private String getLocationString()
  {
    if (locator == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    String str = locator.getSystemId();
    int i = locator.getLineNumber();
    int j = locator.getColumnNumber();
    if (null != str)
    {
      localStringBuilder.append("; SystemID: ");
      localStringBuilder.append(str);
    }
    if (0 != i)
    {
      localStringBuilder.append("; Line#: ");
      localStringBuilder.append(i);
    }
    if (0 != j)
    {
      localStringBuilder.append("; Column#: ");
      localStringBuilder.append(j);
    }
    return localStringBuilder.toString();
  }
  
  public void printStackTrace()
  {
    printStackTrace(new PrintWriter(System.err, true));
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    printStackTrace(new PrintWriter(paramPrintStream));
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    if (paramPrintWriter == null) {
      paramPrintWriter = new PrintWriter(System.err, true);
    }
    try
    {
      String str1 = getLocationAsString();
      if (null != str1) {
        paramPrintWriter.println(str1);
      }
      super.printStackTrace(paramPrintWriter);
    }
    catch (Throwable localThrowable1) {}
    Throwable localThrowable2 = getException();
    for (int i = 0; (i < 10) && (null != localThrowable2); i++)
    {
      paramPrintWriter.println("---------");
      try
      {
        if ((localThrowable2 instanceof TransformerException))
        {
          String str2 = ((TransformerException)localThrowable2).getLocationAsString();
          if (null != str2) {
            paramPrintWriter.println(str2);
          }
        }
        localThrowable2.printStackTrace(paramPrintWriter);
      }
      catch (Throwable localThrowable3)
      {
        paramPrintWriter.println("Could not print stack trace...");
      }
      try
      {
        Method localMethod = localThrowable2.getClass().getMethod("getException", (Class[])null);
        if (null != localMethod)
        {
          Throwable localThrowable4 = localThrowable2;
          localThrowable2 = (Throwable)localMethod.invoke(localThrowable2, (Object[])null);
          if (localThrowable4 == localThrowable2) {
            break;
          }
        }
        else
        {
          localThrowable2 = null;
        }
      }
      catch (InvocationTargetException|IllegalAccessException|NoSuchMethodException localInvocationTargetException)
      {
        localThrowable2 = null;
      }
    }
    paramPrintWriter.flush();
  }
  
  private ProtectionDomain getNonPrivDomain()
  {
    CodeSource localCodeSource = new CodeSource(null, (CodeSigner[])null);
    Permissions localPermissions = new Permissions();
    return new ProtectionDomain(localCodeSource, localPermissions);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\TransformerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */