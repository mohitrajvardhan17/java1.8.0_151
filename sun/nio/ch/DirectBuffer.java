package sun.nio.ch;

import sun.misc.Cleaner;

public abstract interface DirectBuffer
{
  public abstract long address();
  
  public abstract Object attachment();
  
  public abstract Cleaner cleaner();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\DirectBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */