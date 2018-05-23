package java.util;

class TaskQueue
{
  private TimerTask[] queue = new TimerTask[''];
  private int size = 0;
  
  TaskQueue() {}
  
  int size()
  {
    return size;
  }
  
  void add(TimerTask paramTimerTask)
  {
    if (size + 1 == queue.length) {
      queue = ((TimerTask[])Arrays.copyOf(queue, 2 * queue.length));
    }
    queue[(++size)] = paramTimerTask;
    fixUp(size);
  }
  
  TimerTask getMin()
  {
    return queue[1];
  }
  
  TimerTask get(int paramInt)
  {
    return queue[paramInt];
  }
  
  void removeMin()
  {
    queue[1] = queue[size];
    queue[(size--)] = null;
    fixDown(1);
  }
  
  void quickRemove(int paramInt)
  {
    assert (paramInt <= size);
    queue[paramInt] = queue[size];
    queue[(size--)] = null;
  }
  
  void rescheduleMin(long paramLong)
  {
    queue[1].nextExecutionTime = paramLong;
    fixDown(1);
  }
  
  boolean isEmpty()
  {
    return size == 0;
  }
  
  void clear()
  {
    for (int i = 1; i <= size; i++) {
      queue[i] = null;
    }
    size = 0;
  }
  
  private void fixUp(int paramInt)
  {
    while (paramInt > 1)
    {
      int i = paramInt >> 1;
      if (queue[i].nextExecutionTime <= queue[paramInt].nextExecutionTime) {
        break;
      }
      TimerTask localTimerTask = queue[i];
      queue[i] = queue[paramInt];
      queue[paramInt] = localTimerTask;
      paramInt = i;
    }
  }
  
  private void fixDown(int paramInt)
  {
    int i;
    while (((i = paramInt << 1) <= size) && (i > 0))
    {
      if ((i < size) && (queue[i].nextExecutionTime > queue[(i + 1)].nextExecutionTime)) {
        i++;
      }
      if (queue[paramInt].nextExecutionTime <= queue[i].nextExecutionTime) {
        break;
      }
      TimerTask localTimerTask = queue[i];
      queue[i] = queue[paramInt];
      queue[paramInt] = localTimerTask;
      paramInt = i;
    }
  }
  
  void heapify()
  {
    for (int i = size / 2; i >= 1; i--) {
      fixDown(i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\TaskQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */