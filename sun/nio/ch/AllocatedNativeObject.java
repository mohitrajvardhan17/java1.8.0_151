package sun.nio.ch;

import sun.misc.Unsafe;

class AllocatedNativeObject
  extends NativeObject
{
  AllocatedNativeObject(int paramInt, boolean paramBoolean)
  {
    super(paramInt, paramBoolean);
  }
  
  synchronized void free()
  {
    if (allocationAddress != 0L)
    {
      unsafe.freeMemory(allocationAddress);
      allocationAddress = 0L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\AllocatedNativeObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */