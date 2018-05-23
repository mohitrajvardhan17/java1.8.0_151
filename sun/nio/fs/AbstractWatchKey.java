package sun.nio.fs;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

abstract class AbstractWatchKey
  implements WatchKey
{
  static final int MAX_EVENT_LIST_SIZE = 512;
  static final Event<Object> OVERFLOW_EVENT = new Event(StandardWatchEventKinds.OVERFLOW, null);
  private final AbstractWatchService watcher;
  private final Path dir;
  private State state;
  private List<WatchEvent<?>> events;
  private Map<Object, WatchEvent<?>> lastModifyEvents;
  
  protected AbstractWatchKey(Path paramPath, AbstractWatchService paramAbstractWatchService)
  {
    watcher = paramAbstractWatchService;
    dir = paramPath;
    state = State.READY;
    events = new ArrayList();
    lastModifyEvents = new HashMap();
  }
  
  final AbstractWatchService watcher()
  {
    return watcher;
  }
  
  public Path watchable()
  {
    return dir;
  }
  
  final void signal()
  {
    synchronized (this)
    {
      if (state == State.READY)
      {
        state = State.SIGNALLED;
        watcher.enqueueKey(this);
      }
    }
  }
  
  final void signalEvent(WatchEvent.Kind<?> paramKind, Object paramObject)
  {
    int i = paramKind == StandardWatchEventKinds.ENTRY_MODIFY ? 1 : 0;
    synchronized (this)
    {
      int j = events.size();
      if (j > 0)
      {
        localObject1 = (WatchEvent)events.get(j - 1);
        if ((((WatchEvent)localObject1).kind() == StandardWatchEventKinds.OVERFLOW) || ((paramKind == ((WatchEvent)localObject1).kind()) && (Objects.equals(paramObject, ((WatchEvent)localObject1).context()))))
        {
          ((Event)localObject1).increment();
          return;
        }
        if (!lastModifyEvents.isEmpty()) {
          if (i != 0)
          {
            WatchEvent localWatchEvent = (WatchEvent)lastModifyEvents.get(paramObject);
            if (localWatchEvent != null)
            {
              assert (localWatchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY);
              ((Event)localWatchEvent).increment();
              return;
            }
          }
          else
          {
            lastModifyEvents.remove(paramObject);
          }
        }
        if (j >= 512)
        {
          paramKind = StandardWatchEventKinds.OVERFLOW;
          i = 0;
          paramObject = null;
        }
      }
      Object localObject1 = new Event(paramKind, paramObject);
      if (i != 0)
      {
        lastModifyEvents.put(paramObject, localObject1);
      }
      else if (paramKind == StandardWatchEventKinds.OVERFLOW)
      {
        events.clear();
        lastModifyEvents.clear();
      }
      events.add(localObject1);
      signal();
    }
  }
  
  public final List<WatchEvent<?>> pollEvents()
  {
    synchronized (this)
    {
      List localList = events;
      events = new ArrayList();
      lastModifyEvents.clear();
      return localList;
    }
  }
  
  public final boolean reset()
  {
    synchronized (this)
    {
      if ((state == State.SIGNALLED) && (isValid())) {
        if (events.isEmpty()) {
          state = State.READY;
        } else {
          watcher.enqueueKey(this);
        }
      }
      return isValid();
    }
  }
  
  private static class Event<T>
    implements WatchEvent<T>
  {
    private final WatchEvent.Kind<T> kind;
    private final T context;
    private int count;
    
    Event(WatchEvent.Kind<T> paramKind, T paramT)
    {
      kind = paramKind;
      context = paramT;
      count = 1;
    }
    
    public WatchEvent.Kind<T> kind()
    {
      return kind;
    }
    
    public T context()
    {
      return (T)context;
    }
    
    public int count()
    {
      return count;
    }
    
    void increment()
    {
      count += 1;
    }
  }
  
  private static enum State
  {
    READY,  SIGNALLED;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\AbstractWatchKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */