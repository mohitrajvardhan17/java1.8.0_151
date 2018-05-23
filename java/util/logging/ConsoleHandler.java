package java.util.logging;

public class ConsoleHandler
  extends StreamHandler
{
  private void configure()
  {
    LogManager localLogManager = LogManager.getLogManager();
    String str = getClass().getName();
    setLevel(localLogManager.getLevelProperty(str + ".level", Level.INFO));
    setFilter(localLogManager.getFilterProperty(str + ".filter", null));
    setFormatter(localLogManager.getFormatterProperty(str + ".formatter", new SimpleFormatter()));
    try
    {
      setEncoding(localLogManager.getStringProperty(str + ".encoding", null));
    }
    catch (Exception localException1)
    {
      try
      {
        setEncoding(null);
      }
      catch (Exception localException2) {}
    }
  }
  
  public ConsoleHandler()
  {
    sealed = false;
    configure();
    setOutputStream(System.err);
    sealed = true;
  }
  
  public void publish(LogRecord paramLogRecord)
  {
    super.publish(paramLogRecord);
    flush();
  }
  
  public void close()
  {
    flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\ConsoleHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */