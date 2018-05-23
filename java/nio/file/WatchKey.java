package java.nio.file;

import java.util.List;

public abstract interface WatchKey
{
  public abstract boolean isValid();
  
  public abstract List<WatchEvent<?>> pollEvents();
  
  public abstract boolean reset();
  
  public abstract void cancel();
  
  public abstract Watchable watchable();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\WatchKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */