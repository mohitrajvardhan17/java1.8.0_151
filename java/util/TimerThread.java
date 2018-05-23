package java.util;

class TimerThread
  extends Thread
{
  boolean newTasksMayBeScheduled = true;
  private TaskQueue queue;
  
  TimerThread(TaskQueue paramTaskQueue)
  {
    queue = paramTaskQueue;
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 79	java/util/TimerThread:mainLoop	()V
    //   4: aload_0
    //   5: getfield 68	java/util/TimerThread:queue	Ljava/util/TaskQueue;
    //   8: dup
    //   9: astore_1
    //   10: monitorenter
    //   11: aload_0
    //   12: iconst_0
    //   13: putfield 67	java/util/TimerThread:newTasksMayBeScheduled	Z
    //   16: aload_0
    //   17: getfield 68	java/util/TimerThread:queue	Ljava/util/TaskQueue;
    //   20: invokevirtual 73	java/util/TaskQueue:clear	()V
    //   23: aload_1
    //   24: monitorexit
    //   25: goto +8 -> 33
    //   28: astore_2
    //   29: aload_1
    //   30: monitorexit
    //   31: aload_2
    //   32: athrow
    //   33: goto +40 -> 73
    //   36: astore_3
    //   37: aload_0
    //   38: getfield 68	java/util/TimerThread:queue	Ljava/util/TaskQueue;
    //   41: dup
    //   42: astore 4
    //   44: monitorenter
    //   45: aload_0
    //   46: iconst_0
    //   47: putfield 67	java/util/TimerThread:newTasksMayBeScheduled	Z
    //   50: aload_0
    //   51: getfield 68	java/util/TimerThread:queue	Ljava/util/TaskQueue;
    //   54: invokevirtual 73	java/util/TaskQueue:clear	()V
    //   57: aload 4
    //   59: monitorexit
    //   60: goto +11 -> 71
    //   63: astore 5
    //   65: aload 4
    //   67: monitorexit
    //   68: aload 5
    //   70: athrow
    //   71: aload_3
    //   72: athrow
    //   73: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	TimerThread
    //   28	4	2	localObject1	Object
    //   36	36	3	localObject2	Object
    //   63	6	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   11	25	28	finally
    //   28	31	28	finally
    //   0	4	36	finally
    //   45	60	63	finally
    //   63	68	63	finally
  }
  
  private void mainLoop()
  {
    try
    {
      for (;;)
      {
        TimerTask localTimerTask;
        int i;
        synchronized (queue)
        {
          if ((queue.isEmpty()) && (newTasksMayBeScheduled))
          {
            queue.wait();
            continue;
          }
          if (queue.isEmpty()) {
            break;
          }
          localTimerTask = queue.getMin();
          long l1;
          long l2;
          synchronized (lock)
          {
            if (state == 3)
            {
              queue.removeMin();
              continue;
            }
            l1 = System.currentTimeMillis();
            l2 = nextExecutionTime;
            if ((i = l2 <= l1 ? 1 : 0) != 0) {
              if (period == 0L)
              {
                queue.removeMin();
                state = 2;
              }
              else
              {
                queue.rescheduleMin(period < 0L ? l1 - period : l2 + period);
              }
            }
          }
          if (i == 0) {
            queue.wait(l2 - l1);
          }
        }
        if (i != 0) {
          localTimerTask.run();
        }
      }
    }
    catch (InterruptedException localInterruptedException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\TimerThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */