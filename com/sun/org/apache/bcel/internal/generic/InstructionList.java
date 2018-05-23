package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InstructionList
  implements Serializable
{
  private InstructionHandle start = null;
  private InstructionHandle end = null;
  private int length = 0;
  private int[] byte_positions;
  private ArrayList observers;
  
  public InstructionList() {}
  
  public InstructionList(Instruction paramInstruction)
  {
    append(paramInstruction);
  }
  
  public InstructionList(BranchInstruction paramBranchInstruction)
  {
    append(paramBranchInstruction);
  }
  
  public InstructionList(CompoundInstruction paramCompoundInstruction)
  {
    append(paramCompoundInstruction.getInstructionList());
  }
  
  public boolean isEmpty()
  {
    return start == null;
  }
  
  public static InstructionHandle findHandle(InstructionHandle[] paramArrayOfInstructionHandle, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = paramInt1 - 1;
    do
    {
      int k = (i + j) / 2;
      int m = paramArrayOfInt[k];
      if (m == paramInt2) {
        return paramArrayOfInstructionHandle[k];
      }
      if (paramInt2 < m) {
        j = k - 1;
      } else {
        i = k + 1;
      }
    } while (i <= j);
    return null;
  }
  
  public InstructionHandle findHandle(int paramInt)
  {
    InstructionHandle[] arrayOfInstructionHandle = getInstructionHandles();
    return findHandle(arrayOfInstructionHandle, byte_positions, length, paramInt);
  }
  
  public InstructionList(byte[] paramArrayOfByte)
  {
    ByteSequence localByteSequence = new ByteSequence(paramArrayOfByte);
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[paramArrayOfByte.length];
    int[] arrayOfInt1 = new int[paramArrayOfByte.length];
    int i = 0;
    Object localObject1;
    try
    {
      while (localByteSequence.available() > 0)
      {
        int j = localByteSequence.getIndex();
        arrayOfInt1[i] = j;
        localObject1 = Instruction.readInstruction(localByteSequence);
        Object localObject2;
        if ((localObject1 instanceof BranchInstruction)) {
          localObject2 = append((BranchInstruction)localObject1);
        } else {
          localObject2 = append((Instruction)localObject1);
        }
        ((InstructionHandle)localObject2).setPosition(j);
        arrayOfInstructionHandle[i] = localObject2;
        i++;
      }
    }
    catch (IOException localIOException)
    {
      throw new ClassGenException(localIOException.toString());
    }
    byte_positions = new int[i];
    System.arraycopy(arrayOfInt1, 0, byte_positions, 0, i);
    for (int k = 0; k < i; k++) {
      if ((arrayOfInstructionHandle[k] instanceof BranchHandle))
      {
        localObject1 = (BranchInstruction)instruction;
        int m = position + ((BranchInstruction)localObject1).getIndex();
        InstructionHandle localInstructionHandle = findHandle(arrayOfInstructionHandle, arrayOfInt1, i, m);
        if (localInstructionHandle == null) {
          throw new ClassGenException("Couldn't find target for branch: " + localObject1);
        }
        ((BranchInstruction)localObject1).setTarget(localInstructionHandle);
        if ((localObject1 instanceof Select))
        {
          Select localSelect = (Select)localObject1;
          int[] arrayOfInt2 = localSelect.getIndices();
          for (int n = 0; n < arrayOfInt2.length; n++)
          {
            m = position + arrayOfInt2[n];
            localInstructionHandle = findHandle(arrayOfInstructionHandle, arrayOfInt1, i, m);
            if (localInstructionHandle == null) {
              throw new ClassGenException("Couldn't find target for switch: " + localObject1);
            }
            localSelect.setTarget(n, localInstructionHandle);
          }
        }
      }
    }
  }
  
  public InstructionHandle append(InstructionHandle paramInstructionHandle, InstructionList paramInstructionList)
  {
    if (paramInstructionList == null) {
      throw new ClassGenException("Appending null InstructionList");
    }
    if (paramInstructionList.isEmpty()) {
      return paramInstructionHandle;
    }
    InstructionHandle localInstructionHandle1 = next;
    InstructionHandle localInstructionHandle2 = start;
    next = start;
    start.prev = paramInstructionHandle;
    end.next = localInstructionHandle1;
    if (localInstructionHandle1 != null) {
      prev = end;
    } else {
      end = end;
    }
    length += length;
    paramInstructionList.clear();
    return localInstructionHandle2;
  }
  
  public InstructionHandle append(Instruction paramInstruction, InstructionList paramInstructionList)
  {
    InstructionHandle localInstructionHandle;
    if ((localInstructionHandle = findInstruction2(paramInstruction)) == null) {
      throw new ClassGenException("Instruction " + paramInstruction + " is not contained in this list.");
    }
    return append(localInstructionHandle, paramInstructionList);
  }
  
  public InstructionHandle append(InstructionList paramInstructionList)
  {
    if (paramInstructionList == null) {
      throw new ClassGenException("Appending null InstructionList");
    }
    if (paramInstructionList.isEmpty()) {
      return null;
    }
    if (isEmpty())
    {
      start = start;
      end = end;
      length = length;
      paramInstructionList.clear();
      return start;
    }
    return append(end, paramInstructionList);
  }
  
  private void append(InstructionHandle paramInstructionHandle)
  {
    if (isEmpty())
    {
      start = (end = paramInstructionHandle);
      next = (prev = null);
    }
    else
    {
      end.next = paramInstructionHandle;
      prev = end;
      next = null;
      end = paramInstructionHandle;
    }
    length += 1;
  }
  
  public InstructionHandle append(Instruction paramInstruction)
  {
    InstructionHandle localInstructionHandle = InstructionHandle.getInstructionHandle(paramInstruction);
    append(localInstructionHandle);
    return localInstructionHandle;
  }
  
  public BranchHandle append(BranchInstruction paramBranchInstruction)
  {
    BranchHandle localBranchHandle = BranchHandle.getBranchHandle(paramBranchInstruction);
    append(localBranchHandle);
    return localBranchHandle;
  }
  
  public InstructionHandle append(Instruction paramInstruction1, Instruction paramInstruction2)
  {
    return append(paramInstruction1, new InstructionList(paramInstruction2));
  }
  
  public InstructionHandle append(Instruction paramInstruction, CompoundInstruction paramCompoundInstruction)
  {
    return append(paramInstruction, paramCompoundInstruction.getInstructionList());
  }
  
  public InstructionHandle append(CompoundInstruction paramCompoundInstruction)
  {
    return append(paramCompoundInstruction.getInstructionList());
  }
  
  public InstructionHandle append(InstructionHandle paramInstructionHandle, CompoundInstruction paramCompoundInstruction)
  {
    return append(paramInstructionHandle, paramCompoundInstruction.getInstructionList());
  }
  
  public InstructionHandle append(InstructionHandle paramInstructionHandle, Instruction paramInstruction)
  {
    return append(paramInstructionHandle, new InstructionList(paramInstruction));
  }
  
  public BranchHandle append(InstructionHandle paramInstructionHandle, BranchInstruction paramBranchInstruction)
  {
    BranchHandle localBranchHandle = BranchHandle.getBranchHandle(paramBranchInstruction);
    InstructionList localInstructionList = new InstructionList();
    localInstructionList.append(localBranchHandle);
    append(paramInstructionHandle, localInstructionList);
    return localBranchHandle;
  }
  
  public InstructionHandle insert(InstructionHandle paramInstructionHandle, InstructionList paramInstructionList)
  {
    if (paramInstructionList == null) {
      throw new ClassGenException("Inserting null InstructionList");
    }
    if (paramInstructionList.isEmpty()) {
      return paramInstructionHandle;
    }
    InstructionHandle localInstructionHandle1 = prev;
    InstructionHandle localInstructionHandle2 = start;
    prev = end;
    end.next = paramInstructionHandle;
    start.prev = localInstructionHandle1;
    if (localInstructionHandle1 != null) {
      next = start;
    } else {
      start = start;
    }
    length += length;
    paramInstructionList.clear();
    return localInstructionHandle2;
  }
  
  public InstructionHandle insert(InstructionList paramInstructionList)
  {
    if (isEmpty())
    {
      append(paramInstructionList);
      return start;
    }
    return insert(start, paramInstructionList);
  }
  
  private void insert(InstructionHandle paramInstructionHandle)
  {
    if (isEmpty())
    {
      start = (end = paramInstructionHandle);
      next = (prev = null);
    }
    else
    {
      start.prev = paramInstructionHandle;
      next = start;
      prev = null;
      start = paramInstructionHandle;
    }
    length += 1;
  }
  
  public InstructionHandle insert(Instruction paramInstruction, InstructionList paramInstructionList)
  {
    InstructionHandle localInstructionHandle;
    if ((localInstructionHandle = findInstruction1(paramInstruction)) == null) {
      throw new ClassGenException("Instruction " + paramInstruction + " is not contained in this list.");
    }
    return insert(localInstructionHandle, paramInstructionList);
  }
  
  public InstructionHandle insert(Instruction paramInstruction)
  {
    InstructionHandle localInstructionHandle = InstructionHandle.getInstructionHandle(paramInstruction);
    insert(localInstructionHandle);
    return localInstructionHandle;
  }
  
  public BranchHandle insert(BranchInstruction paramBranchInstruction)
  {
    BranchHandle localBranchHandle = BranchHandle.getBranchHandle(paramBranchInstruction);
    insert(localBranchHandle);
    return localBranchHandle;
  }
  
  public InstructionHandle insert(Instruction paramInstruction1, Instruction paramInstruction2)
  {
    return insert(paramInstruction1, new InstructionList(paramInstruction2));
  }
  
  public InstructionHandle insert(Instruction paramInstruction, CompoundInstruction paramCompoundInstruction)
  {
    return insert(paramInstruction, paramCompoundInstruction.getInstructionList());
  }
  
  public InstructionHandle insert(CompoundInstruction paramCompoundInstruction)
  {
    return insert(paramCompoundInstruction.getInstructionList());
  }
  
  public InstructionHandle insert(InstructionHandle paramInstructionHandle, Instruction paramInstruction)
  {
    return insert(paramInstructionHandle, new InstructionList(paramInstruction));
  }
  
  public InstructionHandle insert(InstructionHandle paramInstructionHandle, CompoundInstruction paramCompoundInstruction)
  {
    return insert(paramInstructionHandle, paramCompoundInstruction.getInstructionList());
  }
  
  public BranchHandle insert(InstructionHandle paramInstructionHandle, BranchInstruction paramBranchInstruction)
  {
    BranchHandle localBranchHandle = BranchHandle.getBranchHandle(paramBranchInstruction);
    InstructionList localInstructionList = new InstructionList();
    localInstructionList.append(localBranchHandle);
    insert(paramInstructionHandle, localInstructionList);
    return localBranchHandle;
  }
  
  public void move(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2, InstructionHandle paramInstructionHandle3)
  {
    if ((paramInstructionHandle1 == null) || (paramInstructionHandle2 == null)) {
      throw new ClassGenException("Invalid null handle: From " + paramInstructionHandle1 + " to " + paramInstructionHandle2);
    }
    if ((paramInstructionHandle3 == paramInstructionHandle1) || (paramInstructionHandle3 == paramInstructionHandle2)) {
      throw new ClassGenException("Invalid range: From " + paramInstructionHandle1 + " to " + paramInstructionHandle2 + " contains target " + paramInstructionHandle3);
    }
    for (InstructionHandle localInstructionHandle1 = paramInstructionHandle1; localInstructionHandle1 != next; localInstructionHandle1 = next)
    {
      if (localInstructionHandle1 == null) {
        throw new ClassGenException("Invalid range: From " + paramInstructionHandle1 + " to " + paramInstructionHandle2);
      }
      if (localInstructionHandle1 == paramInstructionHandle3) {
        throw new ClassGenException("Invalid range: From " + paramInstructionHandle1 + " to " + paramInstructionHandle2 + " contains target " + paramInstructionHandle3);
      }
    }
    localInstructionHandle1 = prev;
    InstructionHandle localInstructionHandle2 = next;
    if (localInstructionHandle1 != null) {
      next = localInstructionHandle2;
    } else {
      start = localInstructionHandle2;
    }
    if (localInstructionHandle2 != null) {
      prev = localInstructionHandle1;
    } else {
      end = localInstructionHandle1;
    }
    prev = (next = null);
    if (paramInstructionHandle3 == null)
    {
      next = start;
      start = paramInstructionHandle1;
    }
    else
    {
      localInstructionHandle2 = next;
      next = paramInstructionHandle1;
      prev = paramInstructionHandle3;
      next = localInstructionHandle2;
      if (localInstructionHandle2 != null) {
        prev = paramInstructionHandle2;
      }
    }
  }
  
  public void move(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    move(paramInstructionHandle1, paramInstructionHandle1, paramInstructionHandle2);
  }
  
  private void remove(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
    throws TargetLostException
  {
    InstructionHandle localInstructionHandle2;
    InstructionHandle localInstructionHandle1;
    if ((paramInstructionHandle1 == null) && (paramInstructionHandle2 == null))
    {
      localInstructionHandle1 = localInstructionHandle2 = start;
      start = (end = null);
    }
    else
    {
      if (paramInstructionHandle1 == null)
      {
        localInstructionHandle1 = start;
        start = paramInstructionHandle2;
      }
      else
      {
        localInstructionHandle1 = next;
        next = paramInstructionHandle2;
      }
      if (paramInstructionHandle2 == null)
      {
        localInstructionHandle2 = end;
        end = paramInstructionHandle1;
      }
      else
      {
        localInstructionHandle2 = prev;
        prev = paramInstructionHandle1;
      }
    }
    prev = null;
    next = null;
    ArrayList localArrayList = new ArrayList();
    for (Object localObject1 = localInstructionHandle1; localObject1 != null; localObject1 = next) {
      ((InstructionHandle)localObject1).getInstruction().dispose();
    }
    localObject1 = new StringBuffer("{ ");
    for (Object localObject2 = localInstructionHandle1; localObject2 != null; localObject2 = paramInstructionHandle2)
    {
      paramInstructionHandle2 = next;
      length -= 1;
      if (((InstructionHandle)localObject2).hasTargeters())
      {
        localArrayList.add(localObject2);
        ((StringBuffer)localObject1).append(((InstructionHandle)localObject2).toString(true) + " ");
        next = (prev = null);
      }
      else
      {
        ((InstructionHandle)localObject2).dispose();
      }
    }
    ((StringBuffer)localObject1).append("}");
    if (!localArrayList.isEmpty())
    {
      localObject2 = new InstructionHandle[localArrayList.size()];
      localArrayList.toArray((Object[])localObject2);
      throw new TargetLostException((InstructionHandle[])localObject2, ((StringBuffer)localObject1).toString());
    }
  }
  
  public void delete(InstructionHandle paramInstructionHandle)
    throws TargetLostException
  {
    remove(prev, next);
  }
  
  public void delete(Instruction paramInstruction)
    throws TargetLostException
  {
    InstructionHandle localInstructionHandle;
    if ((localInstructionHandle = findInstruction1(paramInstruction)) == null) {
      throw new ClassGenException("Instruction " + paramInstruction + " is not contained in this list.");
    }
    delete(localInstructionHandle);
  }
  
  public void delete(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
    throws TargetLostException
  {
    remove(prev, next);
  }
  
  public void delete(Instruction paramInstruction1, Instruction paramInstruction2)
    throws TargetLostException
  {
    InstructionHandle localInstructionHandle1;
    if ((localInstructionHandle1 = findInstruction1(paramInstruction1)) == null) {
      throw new ClassGenException("Instruction " + paramInstruction1 + " is not contained in this list.");
    }
    InstructionHandle localInstructionHandle2;
    if ((localInstructionHandle2 = findInstruction2(paramInstruction2)) == null) {
      throw new ClassGenException("Instruction " + paramInstruction2 + " is not contained in this list.");
    }
    delete(localInstructionHandle1, localInstructionHandle2);
  }
  
  private InstructionHandle findInstruction1(Instruction paramInstruction)
  {
    for (InstructionHandle localInstructionHandle = start; localInstructionHandle != null; localInstructionHandle = next) {
      if (instruction == paramInstruction) {
        return localInstructionHandle;
      }
    }
    return null;
  }
  
  private InstructionHandle findInstruction2(Instruction paramInstruction)
  {
    for (InstructionHandle localInstructionHandle = end; localInstructionHandle != null; localInstructionHandle = prev) {
      if (instruction == paramInstruction) {
        return localInstructionHandle;
      }
    }
    return null;
  }
  
  public boolean contains(InstructionHandle paramInstructionHandle)
  {
    if (paramInstructionHandle == null) {
      return false;
    }
    for (InstructionHandle localInstructionHandle = start; localInstructionHandle != null; localInstructionHandle = next) {
      if (localInstructionHandle == paramInstructionHandle) {
        return true;
      }
    }
    return false;
  }
  
  public boolean contains(Instruction paramInstruction)
  {
    return findInstruction1(paramInstruction) != null;
  }
  
  public void setPositions()
  {
    setPositions(false);
  }
  
  public void setPositions(boolean paramBoolean)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int[] arrayOfInt = new int[length];
    Instruction localInstruction1;
    if (paramBoolean) {
      for (localInstructionHandle = start; localInstructionHandle != null; localInstructionHandle = next)
      {
        localInstruction1 = instruction;
        if ((localInstruction1 instanceof BranchInstruction))
        {
          Instruction localInstruction2 = getTargetinstruction;
          if (!contains(localInstruction2)) {
            throw new ClassGenException("Branch target of " + com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[opcode] + ":" + localInstruction2 + " not in instruction list");
          }
          if ((localInstruction1 instanceof Select))
          {
            InstructionHandle[] arrayOfInstructionHandle = ((Select)localInstruction1).getTargets();
            for (int n = 0; n < arrayOfInstructionHandle.length; n++)
            {
              localInstruction2 = instruction;
              if (!contains(localInstruction2)) {
                throw new ClassGenException("Branch target of " + com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[opcode] + ":" + localInstruction2 + " not in instruction list");
              }
            }
          }
          if (!(localInstructionHandle instanceof BranchHandle)) {
            throw new ClassGenException("Branch instruction " + com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[opcode] + ":" + localInstruction2 + " not contained in BranchHandle.");
          }
        }
      }
    }
    for (InstructionHandle localInstructionHandle = start; localInstructionHandle != null; localInstructionHandle = next)
    {
      localInstruction1 = instruction;
      localInstructionHandle.setPosition(k);
      arrayOfInt[(m++)] = k;
      switch (localInstruction1.getOpcode())
      {
      case 167: 
      case 168: 
        i += 2;
        break;
      case 170: 
      case 171: 
        i += 3;
      }
      k += localInstruction1.getLength();
    }
    for (localInstructionHandle = start; localInstructionHandle != null; localInstructionHandle = next) {
      j += localInstructionHandle.updatePosition(j, i);
    }
    k = m = 0;
    for (localInstructionHandle = start; localInstructionHandle != null; localInstructionHandle = next)
    {
      localInstruction1 = instruction;
      localInstructionHandle.setPosition(k);
      arrayOfInt[(m++)] = k;
      k += localInstruction1.getLength();
    }
    byte_positions = new int[m];
    System.arraycopy(arrayOfInt, 0, byte_positions, 0, m);
  }
  
  public byte[] getByteCode()
  {
    setPositions();
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    try
    {
      for (InstructionHandle localInstructionHandle = start; localInstructionHandle != null; localInstructionHandle = next)
      {
        Instruction localInstruction = instruction;
        localInstruction.dump(localDataOutputStream);
      }
    }
    catch (IOException localIOException)
    {
      System.err.println(localIOException);
      return null;
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  public Instruction[] getInstructions()
  {
    ByteSequence localByteSequence = new ByteSequence(getByteCode());
    ArrayList localArrayList = new ArrayList();
    try
    {
      while (localByteSequence.available() > 0) {
        localArrayList.add(Instruction.readInstruction(localByteSequence));
      }
    }
    catch (IOException localIOException)
    {
      throw new ClassGenException(localIOException.toString());
    }
    Instruction[] arrayOfInstruction = new Instruction[localArrayList.size()];
    localArrayList.toArray(arrayOfInstruction);
    return arrayOfInstruction;
  }
  
  public String toString()
  {
    return toString(true);
  }
  
  public String toString(boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (InstructionHandle localInstructionHandle = start; localInstructionHandle != null; localInstructionHandle = next) {
      localStringBuffer.append(localInstructionHandle.toString(paramBoolean) + "\n");
    }
    return localStringBuffer.toString();
  }
  
  public Iterator iterator()
  {
    new Iterator()
    {
      private InstructionHandle ih = start;
      
      public Object next()
      {
        InstructionHandle localInstructionHandle = ih;
        ih = ih.next;
        return localInstructionHandle;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
      
      public boolean hasNext()
      {
        return ih != null;
      }
    };
  }
  
  public InstructionHandle[] getInstructionHandles()
  {
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[length];
    InstructionHandle localInstructionHandle = start;
    for (int i = 0; i < length; i++)
    {
      arrayOfInstructionHandle[i] = localInstructionHandle;
      localInstructionHandle = next;
    }
    return arrayOfInstructionHandle;
  }
  
  public int[] getInstructionPositions()
  {
    return byte_positions;
  }
  
  public InstructionList copy()
  {
    HashMap localHashMap = new HashMap();
    InstructionList localInstructionList = new InstructionList();
    Instruction localInstruction1;
    for (InstructionHandle localInstructionHandle1 = start; localInstructionHandle1 != null; localInstructionHandle1 = next)
    {
      localObject = instruction;
      localInstruction1 = ((Instruction)localObject).copy();
      if ((localInstruction1 instanceof BranchInstruction)) {
        localHashMap.put(localInstructionHandle1, localInstructionList.append((BranchInstruction)localInstruction1));
      } else {
        localHashMap.put(localInstructionHandle1, localInstructionList.append(localInstruction1));
      }
    }
    localInstructionHandle1 = start;
    for (Object localObject = start; localInstructionHandle1 != null; localObject = next)
    {
      localInstruction1 = instruction;
      Instruction localInstruction2 = instruction;
      if ((localInstruction1 instanceof BranchInstruction))
      {
        BranchInstruction localBranchInstruction1 = (BranchInstruction)localInstruction1;
        BranchInstruction localBranchInstruction2 = (BranchInstruction)localInstruction2;
        InstructionHandle localInstructionHandle2 = localBranchInstruction1.getTarget();
        localBranchInstruction2.setTarget((InstructionHandle)localHashMap.get(localInstructionHandle2));
        if ((localBranchInstruction1 instanceof Select))
        {
          InstructionHandle[] arrayOfInstructionHandle1 = ((Select)localBranchInstruction1).getTargets();
          InstructionHandle[] arrayOfInstructionHandle2 = ((Select)localBranchInstruction2).getTargets();
          for (int i = 0; i < arrayOfInstructionHandle1.length; i++) {
            arrayOfInstructionHandle2[i] = ((InstructionHandle)localHashMap.get(arrayOfInstructionHandle1[i]));
          }
        }
      }
      localInstructionHandle1 = next;
    }
    return localInstructionList;
  }
  
  public void replaceConstantPool(ConstantPoolGen paramConstantPoolGen1, ConstantPoolGen paramConstantPoolGen2)
  {
    for (InstructionHandle localInstructionHandle = start; localInstructionHandle != null; localInstructionHandle = next)
    {
      Instruction localInstruction = instruction;
      if ((localInstruction instanceof CPInstruction))
      {
        CPInstruction localCPInstruction = (CPInstruction)localInstruction;
        Constant localConstant = paramConstantPoolGen1.getConstant(localCPInstruction.getIndex());
        localCPInstruction.setIndex(paramConstantPoolGen2.addConstant(localConstant, paramConstantPoolGen1));
      }
    }
  }
  
  private void clear()
  {
    start = (end = null);
    length = 0;
  }
  
  public void dispose()
  {
    for (InstructionHandle localInstructionHandle = end; localInstructionHandle != null; localInstructionHandle = prev) {
      localInstructionHandle.dispose();
    }
    clear();
  }
  
  public InstructionHandle getStart()
  {
    return start;
  }
  
  public InstructionHandle getEnd()
  {
    return end;
  }
  
  public int getLength()
  {
    return length;
  }
  
  public int size()
  {
    return length;
  }
  
  public void redirectBranches(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    for (InstructionHandle localInstructionHandle1 = start; localInstructionHandle1 != null; localInstructionHandle1 = next)
    {
      Instruction localInstruction = localInstructionHandle1.getInstruction();
      if ((localInstruction instanceof BranchInstruction))
      {
        BranchInstruction localBranchInstruction = (BranchInstruction)localInstruction;
        InstructionHandle localInstructionHandle2 = localBranchInstruction.getTarget();
        if (localInstructionHandle2 == paramInstructionHandle1) {
          localBranchInstruction.setTarget(paramInstructionHandle2);
        }
        if ((localBranchInstruction instanceof Select))
        {
          InstructionHandle[] arrayOfInstructionHandle = ((Select)localBranchInstruction).getTargets();
          for (int i = 0; i < arrayOfInstructionHandle.length; i++) {
            if (arrayOfInstructionHandle[i] == paramInstructionHandle1) {
              ((Select)localBranchInstruction).setTarget(i, paramInstructionHandle2);
            }
          }
        }
      }
    }
  }
  
  public void redirectLocalVariables(LocalVariableGen[] paramArrayOfLocalVariableGen, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    for (int i = 0; i < paramArrayOfLocalVariableGen.length; i++)
    {
      InstructionHandle localInstructionHandle1 = paramArrayOfLocalVariableGen[i].getStart();
      InstructionHandle localInstructionHandle2 = paramArrayOfLocalVariableGen[i].getEnd();
      if (localInstructionHandle1 == paramInstructionHandle1) {
        paramArrayOfLocalVariableGen[i].setStart(paramInstructionHandle2);
      }
      if (localInstructionHandle2 == paramInstructionHandle1) {
        paramArrayOfLocalVariableGen[i].setEnd(paramInstructionHandle2);
      }
    }
  }
  
  public void redirectExceptionHandlers(CodeExceptionGen[] paramArrayOfCodeExceptionGen, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    for (int i = 0; i < paramArrayOfCodeExceptionGen.length; i++)
    {
      if (paramArrayOfCodeExceptionGen[i].getStartPC() == paramInstructionHandle1) {
        paramArrayOfCodeExceptionGen[i].setStartPC(paramInstructionHandle2);
      }
      if (paramArrayOfCodeExceptionGen[i].getEndPC() == paramInstructionHandle1) {
        paramArrayOfCodeExceptionGen[i].setEndPC(paramInstructionHandle2);
      }
      if (paramArrayOfCodeExceptionGen[i].getHandlerPC() == paramInstructionHandle1) {
        paramArrayOfCodeExceptionGen[i].setHandlerPC(paramInstructionHandle2);
      }
    }
  }
  
  public void addObserver(InstructionListObserver paramInstructionListObserver)
  {
    if (observers == null) {
      observers = new ArrayList();
    }
    observers.add(paramInstructionListObserver);
  }
  
  public void removeObserver(InstructionListObserver paramInstructionListObserver)
  {
    if (observers != null) {
      observers.remove(paramInstructionListObserver);
    }
  }
  
  public void update()
  {
    if (observers != null)
    {
      Iterator localIterator = observers.iterator();
      while (localIterator.hasNext()) {
        ((InstructionListObserver)localIterator.next()).notify(this);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\InstructionList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */