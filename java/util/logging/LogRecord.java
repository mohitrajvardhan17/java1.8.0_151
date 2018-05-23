package java.util.logging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class LogRecord
  implements Serializable
{
  private static final AtomicLong globalSequenceNumber = new AtomicLong(0L);
  private static final int MIN_SEQUENTIAL_THREAD_ID = 1073741823;
  private static final AtomicInteger nextThreadId = new AtomicInteger(1073741823);
  private static final ThreadLocal<Integer> threadIds = new ThreadLocal();
  private Level level;
  private long sequenceNumber;
  private String sourceClassName;
  private String sourceMethodName;
  private String message;
  private int threadID;
  private long millis;
  private Throwable thrown;
  private String loggerName;
  private String resourceBundleName;
  private transient boolean needToInferCaller;
  private transient Object[] parameters;
  private transient ResourceBundle resourceBundle;
  private static final long serialVersionUID = 5372048053134512534L;
  
  private int defaultThreadID()
  {
    long l = Thread.currentThread().getId();
    if (l < 1073741823L) {
      return (int)l;
    }
    Integer localInteger = (Integer)threadIds.get();
    if (localInteger == null)
    {
      localInteger = Integer.valueOf(nextThreadId.getAndIncrement());
      threadIds.set(localInteger);
    }
    return localInteger.intValue();
  }
  
  public LogRecord(Level paramLevel, String paramString)
  {
    paramLevel.getClass();
    level = paramLevel;
    message = paramString;
    sequenceNumber = globalSequenceNumber.getAndIncrement();
    threadID = defaultThreadID();
    millis = System.currentTimeMillis();
    needToInferCaller = true;
  }
  
  public String getLoggerName()
  {
    return loggerName;
  }
  
  public void setLoggerName(String paramString)
  {
    loggerName = paramString;
  }
  
  public ResourceBundle getResourceBundle()
  {
    return resourceBundle;
  }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle)
  {
    resourceBundle = paramResourceBundle;
  }
  
  public String getResourceBundleName()
  {
    return resourceBundleName;
  }
  
  public void setResourceBundleName(String paramString)
  {
    resourceBundleName = paramString;
  }
  
  public Level getLevel()
  {
    return level;
  }
  
  public void setLevel(Level paramLevel)
  {
    if (paramLevel == null) {
      throw new NullPointerException();
    }
    level = paramLevel;
  }
  
  public long getSequenceNumber()
  {
    return sequenceNumber;
  }
  
  public void setSequenceNumber(long paramLong)
  {
    sequenceNumber = paramLong;
  }
  
  public String getSourceClassName()
  {
    if (needToInferCaller) {
      inferCaller();
    }
    return sourceClassName;
  }
  
  public void setSourceClassName(String paramString)
  {
    sourceClassName = paramString;
    needToInferCaller = false;
  }
  
  public String getSourceMethodName()
  {
    if (needToInferCaller) {
      inferCaller();
    }
    return sourceMethodName;
  }
  
  public void setSourceMethodName(String paramString)
  {
    sourceMethodName = paramString;
    needToInferCaller = false;
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public void setMessage(String paramString)
  {
    message = paramString;
  }
  
  public Object[] getParameters()
  {
    return parameters;
  }
  
  public void setParameters(Object[] paramArrayOfObject)
  {
    parameters = paramArrayOfObject;
  }
  
  public int getThreadID()
  {
    return threadID;
  }
  
  public void setThreadID(int paramInt)
  {
    threadID = paramInt;
  }
  
  public long getMillis()
  {
    return millis;
  }
  
  public void setMillis(long paramLong)
  {
    millis = paramLong;
  }
  
  public Throwable getThrown()
  {
    return thrown;
  }
  
  public void setThrown(Throwable paramThrowable)
  {
    thrown = paramThrowable;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeByte(1);
    paramObjectOutputStream.writeByte(0);
    if (parameters == null)
    {
      paramObjectOutputStream.writeInt(-1);
      return;
    }
    paramObjectOutputStream.writeInt(parameters.length);
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i] == null) {
        paramObjectOutputStream.writeObject(null);
      } else {
        paramObjectOutputStream.writeObject(parameters[i].toString());
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readByte();
    int j = paramObjectInputStream.readByte();
    if (i != 1) {
      throw new IOException("LogRecord: bad version: " + i + "." + j);
    }
    int k = paramObjectInputStream.readInt();
    if (k < -1) {
      throw new NegativeArraySizeException();
    }
    Object localObject;
    if (k == -1)
    {
      parameters = null;
    }
    else if (k < 255)
    {
      parameters = new Object[k];
      for (int m = 0; m < parameters.length; m++) {
        parameters[m] = paramObjectInputStream.readObject();
      }
    }
    else
    {
      localObject = new ArrayList(Math.min(k, 1024));
      for (int n = 0; n < k; n++) {
        ((List)localObject).add(paramObjectInputStream.readObject());
      }
      parameters = ((List)localObject).toArray(new Object[((List)localObject).size()]);
    }
    if (resourceBundleName != null) {
      try
      {
        localObject = ResourceBundle.getBundle(resourceBundleName, Locale.getDefault(), ClassLoader.getSystemClassLoader());
        resourceBundle = ((ResourceBundle)localObject);
      }
      catch (MissingResourceException localMissingResourceException)
      {
        resourceBundle = null;
      }
    }
    needToInferCaller = false;
  }
  
  private void inferCaller()
  {
    needToInferCaller = false;
    JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
    Throwable localThrowable = new Throwable();
    int i = localJavaLangAccess.getStackTraceDepth(localThrowable);
    int j = 1;
    for (int k = 0; k < i; k++)
    {
      StackTraceElement localStackTraceElement = localJavaLangAccess.getStackTraceElement(localThrowable, k);
      String str = localStackTraceElement.getClassName();
      boolean bool = isLoggerImplFrame(str);
      if (j != 0)
      {
        if (bool) {
          j = 0;
        }
      }
      else if ((!bool) && (!str.startsWith("java.lang.reflect.")) && (!str.startsWith("sun.reflect.")))
      {
        setSourceClassName(str);
        setSourceMethodName(localStackTraceElement.getMethodName());
        return;
      }
    }
  }
  
  private boolean isLoggerImplFrame(String paramString)
  {
    return (paramString.equals("java.util.logging.Logger")) || (paramString.startsWith("java.util.logging.LoggingProxyImpl")) || (paramString.startsWith("sun.util.logging."));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\LogRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */