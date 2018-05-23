package java.nio.file;

public final class StandardWatchEventKinds
{
  public static final WatchEvent.Kind<Object> OVERFLOW = new StdWatchEventKind("OVERFLOW", Object.class);
  public static final WatchEvent.Kind<Path> ENTRY_CREATE = new StdWatchEventKind("ENTRY_CREATE", Path.class);
  public static final WatchEvent.Kind<Path> ENTRY_DELETE = new StdWatchEventKind("ENTRY_DELETE", Path.class);
  public static final WatchEvent.Kind<Path> ENTRY_MODIFY = new StdWatchEventKind("ENTRY_MODIFY", Path.class);
  
  private StandardWatchEventKinds() {}
  
  private static class StdWatchEventKind<T>
    implements WatchEvent.Kind<T>
  {
    private final String name;
    private final Class<T> type;
    
    StdWatchEventKind(String paramString, Class<T> paramClass)
    {
      name = paramString;
      type = paramClass;
    }
    
    public String name()
    {
      return name;
    }
    
    public Class<T> type()
    {
      return type;
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\StandardWatchEventKinds.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */