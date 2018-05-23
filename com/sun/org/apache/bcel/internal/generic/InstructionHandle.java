package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class InstructionHandle
  implements Serializable
{
  InstructionHandle next;
  InstructionHandle prev;
  Instruction instruction;
  protected int i_position = -1;
  private HashSet targeters;
  private HashMap attributes;
  private static InstructionHandle ih_list = null;
  
  public final InstructionHandle getNext()
  {
    return next;
  }
  
  public final InstructionHandle getPrev()
  {
    return prev;
  }
  
  public final Instruction getInstruction()
  {
    return instruction;
  }
  
  public void setInstruction(Instruction paramInstruction)
  {
    if (paramInstruction == null) {
      throw new ClassGenException("Assigning null to handle");
    }
    if ((getClass() != BranchHandle.class) && ((paramInstruction instanceof BranchInstruction))) {
      throw new ClassGenException("Assigning branch instruction " + paramInstruction + " to plain handle");
    }
    if (instruction != null) {
      instruction.dispose();
    }
    instruction = paramInstruction;
  }
  
  public Instruction swapInstruction(Instruction paramInstruction)
  {
    Instruction localInstruction = instruction;
    instruction = paramInstruction;
    return localInstruction;
  }
  
  protected InstructionHandle(Instruction paramInstruction)
  {
    setInstruction(paramInstruction);
  }
  
  static final InstructionHandle getInstructionHandle(Instruction paramInstruction)
  {
    if (ih_list == null) {
      return new InstructionHandle(paramInstruction);
    }
    InstructionHandle localInstructionHandle = ih_list;
    ih_list = next;
    localInstructionHandle.setInstruction(paramInstruction);
    return localInstructionHandle;
  }
  
  protected int updatePosition(int paramInt1, int paramInt2)
  {
    i_position += paramInt1;
    return 0;
  }
  
  public int getPosition()
  {
    return i_position;
  }
  
  void setPosition(int paramInt)
  {
    i_position = paramInt;
  }
  
  protected void addHandle()
  {
    next = ih_list;
    ih_list = this;
  }
  
  void dispose()
  {
    next = (prev = null);
    instruction.dispose();
    instruction = null;
    i_position = -1;
    attributes = null;
    removeAllTargeters();
    addHandle();
  }
  
  public void removeAllTargeters()
  {
    if (targeters != null) {
      targeters.clear();
    }
  }
  
  public void removeTargeter(InstructionTargeter paramInstructionTargeter)
  {
    targeters.remove(paramInstructionTargeter);
  }
  
  public void addTargeter(InstructionTargeter paramInstructionTargeter)
  {
    if (targeters == null) {
      targeters = new HashSet();
    }
    targeters.add(paramInstructionTargeter);
  }
  
  public boolean hasTargeters()
  {
    return (targeters != null) && (targeters.size() > 0);
  }
  
  public InstructionTargeter[] getTargeters()
  {
    if (!hasTargeters()) {
      return null;
    }
    InstructionTargeter[] arrayOfInstructionTargeter = new InstructionTargeter[targeters.size()];
    targeters.toArray(arrayOfInstructionTargeter);
    return arrayOfInstructionTargeter;
  }
  
  public String toString(boolean paramBoolean)
  {
    return Utility.format(i_position, 4, false, ' ') + ": " + instruction.toString(paramBoolean);
  }
  
  public String toString()
  {
    return toString(true);
  }
  
  public void addAttribute(Object paramObject1, Object paramObject2)
  {
    if (attributes == null) {
      attributes = new HashMap(3);
    }
    attributes.put(paramObject1, paramObject2);
  }
  
  public void removeAttribute(Object paramObject)
  {
    if (attributes != null) {
      attributes.remove(paramObject);
    }
  }
  
  public Object getAttribute(Object paramObject)
  {
    if (attributes != null) {
      return attributes.get(paramObject);
    }
    return null;
  }
  
  public Collection getAttributes()
  {
    return attributes.values();
  }
  
  public void accept(Visitor paramVisitor)
  {
    instruction.accept(paramVisitor);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\InstructionHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */