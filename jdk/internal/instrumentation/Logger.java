package jdk.internal.instrumentation;

public abstract interface Logger
{
  public abstract void error(String paramString);
  
  public abstract void warn(String paramString);
  
  public abstract void info(String paramString);
  
  public abstract void debug(String paramString);
  
  public abstract void trace(String paramString);
  
  public abstract void error(String paramString, Throwable paramThrowable);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */