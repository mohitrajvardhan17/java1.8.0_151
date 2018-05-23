package com.sun.org.apache.bcel.internal.generic;

public final class SWITCH
  implements CompoundInstruction
{
  private int[] match;
  private InstructionHandle[] targets;
  private Select instruction;
  private int match_length;
  
  public SWITCH(int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle, int paramInt)
  {
    match = ((int[])paramArrayOfInt.clone());
    targets = ((InstructionHandle[])paramArrayOfInstructionHandle.clone());
    if ((match_length = paramArrayOfInt.length) < 2)
    {
      instruction = new TABLESWITCH(paramArrayOfInt, paramArrayOfInstructionHandle, paramInstructionHandle);
    }
    else
    {
      sort(0, match_length - 1);
      if (matchIsOrdered(paramInt))
      {
        fillup(paramInt, paramInstructionHandle);
        instruction = new TABLESWITCH(match, targets, paramInstructionHandle);
      }
      else
      {
        instruction = new LOOKUPSWITCH(match, targets, paramInstructionHandle);
      }
    }
  }
  
  public SWITCH(int[] paramArrayOfInt, InstructionHandle[] paramArrayOfInstructionHandle, InstructionHandle paramInstructionHandle)
  {
    this(paramArrayOfInt, paramArrayOfInstructionHandle, paramInstructionHandle, 1);
  }
  
  private final void fillup(int paramInt, InstructionHandle paramInstructionHandle)
  {
    int i = match_length + match_length * paramInt;
    int[] arrayOfInt = new int[i];
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[i];
    int j = 1;
    arrayOfInt[0] = match[0];
    arrayOfInstructionHandle[0] = targets[0];
    for (int k = 1; k < match_length; k++)
    {
      int m = match[(k - 1)];
      int n = match[k] - m;
      for (int i1 = 1; i1 < n; i1++)
      {
        arrayOfInt[j] = (m + i1);
        arrayOfInstructionHandle[j] = paramInstructionHandle;
        j++;
      }
      arrayOfInt[j] = match[k];
      arrayOfInstructionHandle[j] = targets[k];
      j++;
    }
    match = new int[j];
    targets = new InstructionHandle[j];
    System.arraycopy(arrayOfInt, 0, match, 0, j);
    System.arraycopy(arrayOfInstructionHandle, 0, targets, 0, j);
  }
  
  private final void sort(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    int j = paramInt2;
    int m = match[((paramInt1 + paramInt2) / 2)];
    do
    {
      while (match[i] < m) {
        i++;
      }
      while (m < match[j]) {
        j--;
      }
      if (i <= j)
      {
        int k = match[i];
        match[i] = match[j];
        match[j] = k;
        InstructionHandle localInstructionHandle = targets[i];
        targets[i] = targets[j];
        targets[j] = localInstructionHandle;
        i++;
        j--;
      }
    } while (i <= j);
    if (paramInt1 < j) {
      sort(paramInt1, j);
    }
    if (i < paramInt2) {
      sort(i, paramInt2);
    }
  }
  
  private final boolean matchIsOrdered(int paramInt)
  {
    for (int i = 1; i < match_length; i++) {
      if (match[i] - match[(i - 1)] > paramInt) {
        return false;
      }
    }
    return true;
  }
  
  public final InstructionList getInstructionList()
  {
    return new InstructionList(instruction);
  }
  
  public final Instruction getInstruction()
  {
    return instruction;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\SWITCH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */