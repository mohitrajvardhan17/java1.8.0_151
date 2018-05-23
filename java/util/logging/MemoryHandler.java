package java.util.logging;

public class MemoryHandler
  extends Handler
{
  private static final int DEFAULT_SIZE = 1000;
  private volatile Level pushLevel;
  private int size;
  private Handler target;
  private LogRecord[] buffer;
  int start;
  int count;
  
  private void configure()
  {
    LogManager localLogManager = LogManager.getLogManager();
    String str = getClass().getName();
    pushLevel = localLogManager.getLevelProperty(str + ".push", Level.SEVERE);
    size = localLogManager.getIntProperty(str + ".size", 1000);
    if (size <= 0) {
      size = 1000;
    }
    setLevel(localLogManager.getLevelProperty(str + ".level", Level.ALL));
    setFilter(localLogManager.getFilterProperty(str + ".filter", null));
    setFormatter(localLogManager.getFormatterProperty(str + ".formatter", new SimpleFormatter()));
  }
  
  public MemoryHandler()
  {
    sealed = false;
    configure();
    sealed = true;
    LogManager localLogManager = LogManager.getLogManager();
    String str1 = getClass().getName();
    String str2 = localLogManager.getProperty(str1 + ".target");
    if (str2 == null) {
      throw new RuntimeException("The handler " + str1 + " does not specify a target");
    }
    try
    {
      Class localClass = ClassLoader.getSystemClassLoader().loadClass(str2);
      target = ((Handler)localClass.newInstance());
    }
    catch (ClassNotFoundException|InstantiationException|IllegalAccessException localClassNotFoundException)
    {
      throw new RuntimeException("MemoryHandler can't load handler target \"" + str2 + "\"", localClassNotFoundException);
    }
    init();
  }
  
  private void init()
  {
    buffer = new LogRecord[size];
    start = 0;
    count = 0;
  }
  
  public MemoryHandler(Handler paramHandler, int paramInt, Level paramLevel)
  {
    if ((paramHandler == null) || (paramLevel == null)) {
      throw new NullPointerException();
    }
    if (paramInt <= 0) {
      throw new IllegalArgumentException();
    }
    sealed = false;
    configure();
    sealed = true;
    target = paramHandler;
    pushLevel = paramLevel;
    size = paramInt;
    init();
  }
  
  public synchronized void publish(LogRecord paramLogRecord)
  {
    if (!isLoggable(paramLogRecord)) {
      return;
    }
    int i = (start + count) % buffer.length;
    buffer[i] = paramLogRecord;
    if (count < buffer.length)
    {
      count += 1;
    }
    else
    {
      start += 1;
      start %= buffer.length;
    }
    if (paramLogRecord.getLevel().intValue() >= pushLevel.intValue()) {
      push();
    }
  }
  
  public synchronized void push()
  {
    for (int i = 0; i < count; i++)
    {
      int j = (start + i) % buffer.length;
      LogRecord localLogRecord = buffer[j];
      target.publish(localLogRecord);
    }
    start = 0;
    count = 0;
  }
  
  public void flush()
  {
    target.flush();
  }
  
  public void close()
    throws SecurityException
  {
    target.close();
    setLevel(Level.OFF);
  }
  
  public synchronized void setPushLevel(Level paramLevel)
    throws SecurityException
  {
    if (paramLevel == null) {
      throw new NullPointerException();
    }
    checkPermission();
    pushLevel = paramLevel;
  }
  
  public Level getPushLevel()
  {
    return pushLevel;
  }
  
  public boolean isLoggable(LogRecord paramLogRecord)
  {
    return super.isLoggable(paramLogRecord);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\MemoryHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */