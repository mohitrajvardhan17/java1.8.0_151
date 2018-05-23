package sun.rmi.transport;

import java.rmi.server.UID;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sun.rmi.runtime.RuntimeUtil;
import sun.rmi.runtime.RuntimeUtil.GetInstanceAction;
import sun.security.action.GetLongAction;

public class DGCAckHandler
{
  private static final long dgcAckTimeout = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.ackTimeout", 300000L))).longValue();
  private static final ScheduledExecutorService scheduler = ((RuntimeUtil)AccessController.doPrivileged(new RuntimeUtil.GetInstanceAction())).getScheduler();
  private static final Map<UID, DGCAckHandler> idTable = Collections.synchronizedMap(new HashMap());
  private final UID id;
  private List<Object> objList = new ArrayList();
  private Future<?> task = null;
  
  DGCAckHandler(UID paramUID)
  {
    id = paramUID;
    if (paramUID != null)
    {
      assert (!idTable.containsKey(paramUID));
      idTable.put(paramUID, this);
    }
  }
  
  synchronized void add(Object paramObject)
  {
    if (objList != null) {
      objList.add(paramObject);
    }
  }
  
  synchronized void startTimer()
  {
    if ((objList != null) && (task == null)) {
      task = scheduler.schedule(new Runnable()
      {
        public void run()
        {
          if (id != null) {
            DGCAckHandler.idTable.remove(id);
          }
          release();
        }
      }, dgcAckTimeout, TimeUnit.MILLISECONDS);
    }
  }
  
  synchronized void release()
  {
    if (task != null)
    {
      task.cancel(false);
      task = null;
    }
    objList = null;
  }
  
  public static void received(UID paramUID)
  {
    DGCAckHandler localDGCAckHandler = (DGCAckHandler)idTable.remove(paramUID);
    if (localDGCAckHandler != null) {
      localDGCAckHandler.release();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\DGCAckHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */