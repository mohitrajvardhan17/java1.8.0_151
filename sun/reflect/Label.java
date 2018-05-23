package sun.reflect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Label
{
  private List<PatchInfo> patches = new ArrayList();
  
  public Label() {}
  
  void add(ClassFileAssembler paramClassFileAssembler, short paramShort1, short paramShort2, int paramInt)
  {
    patches.add(new PatchInfo(paramClassFileAssembler, paramShort1, paramShort2, paramInt));
  }
  
  public void bind()
  {
    Iterator localIterator = patches.iterator();
    while (localIterator.hasNext())
    {
      PatchInfo localPatchInfo = (PatchInfo)localIterator.next();
      int i = asm.getLength();
      short s = (short)(i - instrBCI);
      asm.emitShort(patchBCI, s);
      asm.setStack(stackDepth);
    }
  }
  
  static class PatchInfo
  {
    final ClassFileAssembler asm;
    final short instrBCI;
    final short patchBCI;
    final int stackDepth;
    
    PatchInfo(ClassFileAssembler paramClassFileAssembler, short paramShort1, short paramShort2, int paramInt)
    {
      asm = paramClassFileAssembler;
      instrBCI = paramShort1;
      patchBCI = paramShort2;
      stackDepth = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\Label.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */