package java.nio.file;

import java.io.IOException;

public abstract interface Watchable
{
  public abstract WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs)
    throws IOException;
  
  public abstract WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>... paramVarArgs)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\Watchable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */