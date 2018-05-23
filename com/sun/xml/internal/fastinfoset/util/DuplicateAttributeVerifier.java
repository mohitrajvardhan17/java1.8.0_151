package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;

public class DuplicateAttributeVerifier
{
  public static final int MAP_SIZE = 256;
  public int _currentIteration;
  private Entry[] _map;
  public final Entry _poolHead;
  public Entry _poolCurrent;
  private Entry _poolTail = _poolHead = new Entry();
  
  public DuplicateAttributeVerifier() {}
  
  public final void clear()
  {
    _currentIteration = 0;
    for (Entry localEntry = _poolHead; localEntry != null; localEntry = poolNext) {
      iteration = 0;
    }
    reset();
  }
  
  public final void reset()
  {
    _poolCurrent = _poolHead;
    if (_map == null) {
      _map = new Entry['Ā'];
    }
  }
  
  private final void increasePool(int paramInt)
  {
    if (_map == null)
    {
      _map = new Entry['Ā'];
      _poolCurrent = _poolHead;
    }
    else
    {
      Entry localEntry1 = _poolTail;
      for (int i = 0; i < paramInt; i++)
      {
        Entry localEntry2 = new Entry();
        _poolTail.poolNext = localEntry2;
        _poolTail = localEntry2;
      }
      _poolCurrent = poolNext;
    }
  }
  
  public final void checkForDuplicateAttribute(int paramInt1, int paramInt2)
    throws FastInfosetException
  {
    if (_poolCurrent == null) {
      increasePool(16);
    }
    Entry localEntry1 = _poolCurrent;
    _poolCurrent = _poolCurrent.poolNext;
    Entry localEntry2 = _map[paramInt1];
    if ((localEntry2 == null) || (iteration < _currentIteration))
    {
      hashNext = null;
      _map[paramInt1] = localEntry1;
      iteration = _currentIteration;
      value = paramInt2;
    }
    else
    {
      Entry localEntry3 = localEntry2;
      do
      {
        if (value == paramInt2)
        {
          reset();
          throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateAttribute"));
        }
      } while ((localEntry3 = hashNext) != null);
      hashNext = localEntry2;
      _map[paramInt1] = localEntry1;
      iteration = _currentIteration;
      value = paramInt2;
    }
  }
  
  public static class Entry
  {
    private int iteration;
    private int value;
    private Entry hashNext;
    private Entry poolNext;
    
    public Entry() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\DuplicateAttributeVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */