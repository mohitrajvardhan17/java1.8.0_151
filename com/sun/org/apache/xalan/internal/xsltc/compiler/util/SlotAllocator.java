package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.Type;

final class SlotAllocator
{
  private int _firstAvailableSlot;
  private int _size = 8;
  private int _free = 0;
  private int[] _slotsTaken = new int[_size];
  
  SlotAllocator() {}
  
  public void initialize(LocalVariableGen[] paramArrayOfLocalVariableGen)
  {
    int i = paramArrayOfLocalVariableGen.length;
    int j = 0;
    for (int n = 0; n < i; n++)
    {
      int k = paramArrayOfLocalVariableGen[n].getType().getSize();
      int m = paramArrayOfLocalVariableGen[n].getIndex();
      j = Math.max(j, m + k);
    }
    _firstAvailableSlot = j;
  }
  
  public int allocateSlot(Type paramType)
  {
    int i = paramType.getSize();
    int j = _free;
    int k = _firstAvailableSlot;
    int m = 0;
    if (_free + i > _size)
    {
      int[] arrayOfInt = new int[_size *= 2];
      for (int i1 = 0; i1 < j; i1++) {
        arrayOfInt[i1] = _slotsTaken[i1];
      }
      _slotsTaken = arrayOfInt;
    }
    while (m < j)
    {
      if (k + i <= _slotsTaken[m])
      {
        for (n = j - 1; n >= m; n--) {
          _slotsTaken[(n + i)] = _slotsTaken[n];
        }
        break;
      }
      k = _slotsTaken[(m++)] + 1;
    }
    for (int n = 0; n < i; n++) {
      _slotsTaken[(m + n)] = (k + n);
    }
    _free += i;
    return k;
  }
  
  public void releaseSlot(LocalVariableGen paramLocalVariableGen)
  {
    int i = paramLocalVariableGen.getType().getSize();
    int j = paramLocalVariableGen.getIndex();
    int k = _free;
    for (int m = 0; m < k; m++) {
      if (_slotsTaken[m] == j)
      {
        int n = m + i;
        while (n < k) {
          _slotsTaken[(m++)] = _slotsTaken[(n++)];
        }
        _free -= i;
        return;
      }
    }
    String str = "Variable slot allocation error(size=" + i + ", slot=" + j + ", limit=" + k + ")";
    ErrorMsg localErrorMsg = new ErrorMsg("INTERNAL_ERR", str);
    throw new Error(localErrorMsg.toString());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\SlotAllocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */