package sun.nio.ch;

class NativeThreadSet
{
  private long[] elts;
  private int used = 0;
  private boolean waitingToEmpty;
  
  NativeThreadSet(int paramInt)
  {
    elts = new long[paramInt];
  }
  
  int add()
  {
    long l = NativeThread.current();
    if (l == 0L) {
      l = -1L;
    }
    synchronized (this)
    {
      int i = 0;
      if (used >= elts.length)
      {
        j = elts.length;
        int k = j * 2;
        long[] arrayOfLong = new long[k];
        System.arraycopy(elts, 0, arrayOfLong, 0, j);
        elts = arrayOfLong;
        i = j;
      }
      for (int j = i; j < elts.length; j++) {
        if (elts[j] == 0L)
        {
          elts[j] = l;
          used += 1;
          return j;
        }
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      return -1;
    }
  }
  
  void remove(int paramInt)
  {
    synchronized (this)
    {
      elts[paramInt] = 0L;
      used -= 1;
      if ((used == 0) && (waitingToEmpty)) {
        notifyAll();
      }
    }
  }
  
  synchronized void signalAndWait()
  {
    int i = 0;
    while (used > 0)
    {
      int j = used;
      int k = elts.length;
      for (int m = 0; m < k; m++)
      {
        long l = elts[m];
        if (l != 0L)
        {
          if (l != -1L) {
            NativeThread.signal(l);
          }
          j--;
          if (j == 0) {
            break;
          }
        }
      }
      waitingToEmpty = true;
      try
      {
        wait(50L);
      }
      catch (InterruptedException localInterruptedException)
      {
        i = 1;
      }
      finally
      {
        waitingToEmpty = false;
      }
    }
    if (i != 0) {
      Thread.currentThread().interrupt();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\NativeThreadSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */