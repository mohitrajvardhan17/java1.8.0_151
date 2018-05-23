package com.sun.org.apache.bcel.internal.generic;

public final class BranchHandle
  extends InstructionHandle
{
  private BranchInstruction bi;
  private static BranchHandle bh_list = null;
  
  private BranchHandle(BranchInstruction paramBranchInstruction)
  {
    super(paramBranchInstruction);
    bi = paramBranchInstruction;
  }
  
  static final BranchHandle getBranchHandle(BranchInstruction paramBranchInstruction)
  {
    if (bh_list == null) {
      return new BranchHandle(paramBranchInstruction);
    }
    BranchHandle localBranchHandle = bh_list;
    bh_list = (BranchHandle)next;
    localBranchHandle.setInstruction(paramBranchInstruction);
    return localBranchHandle;
  }
  
  protected void addHandle()
  {
    next = bh_list;
    bh_list = this;
  }
  
  public int getPosition()
  {
    return bi.position;
  }
  
  void setPosition(int paramInt)
  {
    i_position = (bi.position = paramInt);
  }
  
  protected int updatePosition(int paramInt1, int paramInt2)
  {
    int i = bi.updatePosition(paramInt1, paramInt2);
    i_position = bi.position;
    return i;
  }
  
  public void setTarget(InstructionHandle paramInstructionHandle)
  {
    bi.setTarget(paramInstructionHandle);
  }
  
  public void updateTarget(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    bi.updateTarget(paramInstructionHandle1, paramInstructionHandle2);
  }
  
  public InstructionHandle getTarget()
  {
    return bi.getTarget();
  }
  
  public void setInstruction(Instruction paramInstruction)
  {
    super.setInstruction(paramInstruction);
    if (!(paramInstruction instanceof BranchInstruction)) {
      throw new ClassGenException("Assigning " + paramInstruction + " to branch handle which is not a branch instruction");
    }
    bi = ((BranchInstruction)paramInstruction);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\BranchHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */