package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.IincInsnNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode;
import jdk.internal.org.objectweb.asm.tree.LabelNode;
import jdk.internal.org.objectweb.asm.tree.LookupSwitchInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.TableSwitchInsnNode;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

public class Analyzer<V extends Value>
  implements Opcodes
{
  private final Interpreter<V> interpreter;
  private int n;
  private InsnList insns;
  private List<TryCatchBlockNode>[] handlers;
  private Frame<V>[] frames;
  private Subroutine[] subroutines;
  private boolean[] queued;
  private int[] queue;
  private int top;
  
  public Analyzer(Interpreter<V> paramInterpreter)
  {
    interpreter = paramInterpreter;
  }
  
  public Frame<V>[] analyze(String paramString, MethodNode paramMethodNode)
    throws AnalyzerException
  {
    if ((access & 0x500) != 0)
    {
      frames = ((Frame[])new Frame[0]);
      return frames;
    }
    n = instructions.size();
    insns = instructions;
    handlers = ((List[])new List[n]);
    frames = ((Frame[])new Frame[n]);
    subroutines = new Subroutine[n];
    queued = new boolean[n];
    queue = new int[n];
    top = 0;
    for (int i = 0; i < tryCatchBlocks.size(); i++)
    {
      localObject1 = (TryCatchBlockNode)tryCatchBlocks.get(i);
      int j = insns.indexOf(start);
      int k = insns.indexOf(end);
      for (int i1 = j; i1 < k; i1++)
      {
        localObject3 = handlers[i1];
        if (localObject3 == null)
        {
          localObject3 = new ArrayList();
          handlers[i1] = localObject3;
        }
        ((List)localObject3).add(localObject1);
      }
    }
    Subroutine localSubroutine1 = new Subroutine(null, maxLocals, null);
    Object localObject1 = new ArrayList();
    HashMap localHashMap = new HashMap();
    findSubroutine(0, localSubroutine1, (List)localObject1);
    while (!((List)localObject1).isEmpty())
    {
      JumpInsnNode localJumpInsnNode1 = (JumpInsnNode)((List)localObject1).remove(0);
      localObject2 = (Subroutine)localHashMap.get(label);
      if (localObject2 == null)
      {
        localObject2 = new Subroutine(label, maxLocals, localJumpInsnNode1);
        localHashMap.put(label, localObject2);
        findSubroutine(insns.indexOf(label), (Subroutine)localObject2, (List)localObject1);
      }
      else
      {
        callers.add(localJumpInsnNode1);
      }
    }
    for (int m = 0; m < n; m++) {
      if ((subroutines[m] != null) && (subroutines[m].start == null)) {
        subroutines[m] = null;
      }
    }
    Frame localFrame1 = newFrame(maxLocals, maxStack);
    Object localObject2 = newFrame(maxLocals, maxStack);
    localFrame1.setReturn(interpreter.newValue(Type.getReturnType(desc)));
    Object localObject3 = Type.getArgumentTypes(desc);
    int i2 = 0;
    if ((access & 0x8) == 0)
    {
      Type localType = Type.getObjectType(paramString);
      localFrame1.setLocal(i2++, interpreter.newValue(localType));
    }
    for (int i3 = 0; i3 < localObject3.length; i3++)
    {
      localFrame1.setLocal(i2++, interpreter.newValue(localObject3[i3]));
      if (localObject3[i3].getSize() == 2) {
        localFrame1.setLocal(i2++, interpreter.newValue(null));
      }
    }
    while (i2 < maxLocals) {
      localFrame1.setLocal(i2++, interpreter.newValue(null));
    }
    merge(0, localFrame1, null);
    init(paramString, paramMethodNode);
    while (top > 0)
    {
      i3 = queue[(--top)];
      Frame localFrame2 = frames[i3];
      Subroutine localSubroutine2 = subroutines[i3];
      queued[i3] = false;
      AbstractInsnNode localAbstractInsnNode = null;
      try
      {
        localAbstractInsnNode = instructions.get(i3);
        int i4 = localAbstractInsnNode.getOpcode();
        int i5 = localAbstractInsnNode.getType();
        Object localObject5;
        if ((i5 == 8) || (i5 == 15) || (i5 == 14))
        {
          merge(i3 + 1, localFrame2, localSubroutine2);
          newControlFlowEdge(i3, i3 + 1);
        }
        else
        {
          localFrame1.init(localFrame2).execute(localAbstractInsnNode, interpreter);
          localSubroutine2 = localSubroutine2 == null ? null : localSubroutine2.copy();
          Object localObject4;
          int i7;
          if ((localAbstractInsnNode instanceof JumpInsnNode))
          {
            localObject4 = (JumpInsnNode)localAbstractInsnNode;
            if ((i4 != 167) && (i4 != 168))
            {
              merge(i3 + 1, localFrame1, localSubroutine2);
              newControlFlowEdge(i3, i3 + 1);
            }
            i7 = insns.indexOf(label);
            if (i4 == 168) {
              merge(i7, localFrame1, new Subroutine(label, maxLocals, (JumpInsnNode)localObject4));
            } else {
              merge(i7, localFrame1, localSubroutine2);
            }
            newControlFlowEdge(i3, i7);
          }
          else
          {
            int i9;
            if ((localAbstractInsnNode instanceof LookupSwitchInsnNode))
            {
              localObject4 = (LookupSwitchInsnNode)localAbstractInsnNode;
              i7 = insns.indexOf(dflt);
              merge(i7, localFrame1, localSubroutine2);
              newControlFlowEdge(i3, i7);
              for (i9 = 0; i9 < labels.size(); i9++)
              {
                localObject5 = (LabelNode)labels.get(i9);
                i7 = insns.indexOf((AbstractInsnNode)localObject5);
                merge(i7, localFrame1, localSubroutine2);
                newControlFlowEdge(i3, i7);
              }
            }
            else if ((localAbstractInsnNode instanceof TableSwitchInsnNode))
            {
              localObject4 = (TableSwitchInsnNode)localAbstractInsnNode;
              i7 = insns.indexOf(dflt);
              merge(i7, localFrame1, localSubroutine2);
              newControlFlowEdge(i3, i7);
              for (i9 = 0; i9 < labels.size(); i9++)
              {
                localObject5 = (LabelNode)labels.get(i9);
                i7 = insns.indexOf((AbstractInsnNode)localObject5);
                merge(i7, localFrame1, localSubroutine2);
                newControlFlowEdge(i3, i7);
              }
            }
            else
            {
              int i6;
              if (i4 == 169)
              {
                if (localSubroutine2 == null) {
                  throw new AnalyzerException(localAbstractInsnNode, "RET instruction outside of a sub routine");
                }
                for (i6 = 0; i6 < callers.size(); i6++)
                {
                  JumpInsnNode localJumpInsnNode2 = (JumpInsnNode)callers.get(i6);
                  i9 = insns.indexOf(localJumpInsnNode2);
                  if (frames[i9] != null)
                  {
                    merge(i9 + 1, frames[i9], localFrame1, subroutines[i9], access);
                    newControlFlowEdge(i3, i9 + 1);
                  }
                }
              }
              else if ((i4 != 191) && ((i4 < 172) || (i4 > 177)))
              {
                if (localSubroutine2 != null) {
                  if ((localAbstractInsnNode instanceof VarInsnNode))
                  {
                    i6 = var;
                    access[i6] = true;
                    if ((i4 == 22) || (i4 == 24) || (i4 == 55) || (i4 == 57)) {
                      access[(i6 + 1)] = true;
                    }
                  }
                  else if ((localAbstractInsnNode instanceof IincInsnNode))
                  {
                    i6 = var;
                    access[i6] = true;
                  }
                }
                merge(i3 + 1, localFrame1, localSubroutine2);
                newControlFlowEdge(i3, i3 + 1);
              }
            }
          }
        }
        List localList = handlers[i3];
        if (localList != null) {
          for (int i8 = 0; i8 < localList.size(); i8++)
          {
            TryCatchBlockNode localTryCatchBlockNode = (TryCatchBlockNode)localList.get(i8);
            if (type == null) {
              localObject5 = Type.getObjectType("java/lang/Throwable");
            } else {
              localObject5 = Type.getObjectType(type);
            }
            int i10 = insns.indexOf(handler);
            if (newControlFlowExceptionEdge(i3, localTryCatchBlockNode))
            {
              ((Frame)localObject2).init(localFrame2);
              ((Frame)localObject2).clearStack();
              ((Frame)localObject2).push(interpreter.newValue((Type)localObject5));
              merge(i10, (Frame)localObject2, localSubroutine2);
            }
          }
        }
      }
      catch (AnalyzerException localAnalyzerException)
      {
        throw new AnalyzerException(node, "Error at instruction " + i3 + ": " + localAnalyzerException.getMessage(), localAnalyzerException);
      }
      catch (Exception localException)
      {
        throw new AnalyzerException(localAbstractInsnNode, "Error at instruction " + i3 + ": " + localException.getMessage(), localException);
      }
    }
    return frames;
  }
  
  private void findSubroutine(int paramInt, Subroutine paramSubroutine, List<AbstractInsnNode> paramList)
    throws AnalyzerException
  {
    for (;;)
    {
      if ((paramInt < 0) || (paramInt >= n)) {
        throw new AnalyzerException(null, "Execution can fall off end of the code");
      }
      if (subroutines[paramInt] != null) {
        return;
      }
      subroutines[paramInt] = paramSubroutine.copy();
      AbstractInsnNode localAbstractInsnNode = insns.get(paramInt);
      int i;
      Object localObject2;
      if ((localAbstractInsnNode instanceof JumpInsnNode))
      {
        if (localAbstractInsnNode.getOpcode() == 168)
        {
          paramList.add(localAbstractInsnNode);
        }
        else
        {
          localObject1 = (JumpInsnNode)localAbstractInsnNode;
          findSubroutine(insns.indexOf(label), paramSubroutine, paramList);
        }
      }
      else if ((localAbstractInsnNode instanceof TableSwitchInsnNode))
      {
        localObject1 = (TableSwitchInsnNode)localAbstractInsnNode;
        findSubroutine(insns.indexOf(dflt), paramSubroutine, paramList);
        for (i = labels.size() - 1; i >= 0; i--)
        {
          localObject2 = (LabelNode)labels.get(i);
          findSubroutine(insns.indexOf((AbstractInsnNode)localObject2), paramSubroutine, paramList);
        }
      }
      else if ((localAbstractInsnNode instanceof LookupSwitchInsnNode))
      {
        localObject1 = (LookupSwitchInsnNode)localAbstractInsnNode;
        findSubroutine(insns.indexOf(dflt), paramSubroutine, paramList);
        for (i = labels.size() - 1; i >= 0; i--)
        {
          localObject2 = (LabelNode)labels.get(i);
          findSubroutine(insns.indexOf((AbstractInsnNode)localObject2), paramSubroutine, paramList);
        }
      }
      Object localObject1 = handlers[paramInt];
      if (localObject1 != null) {
        for (i = 0; i < ((List)localObject1).size(); i++)
        {
          localObject2 = (TryCatchBlockNode)((List)localObject1).get(i);
          findSubroutine(insns.indexOf(handler), paramSubroutine, paramList);
        }
      }
      switch (localAbstractInsnNode.getOpcode())
      {
      case 167: 
      case 169: 
      case 170: 
      case 171: 
      case 172: 
      case 173: 
      case 174: 
      case 175: 
      case 176: 
      case 177: 
      case 191: 
        return;
      }
      paramInt++;
    }
  }
  
  public Frame<V>[] getFrames()
  {
    return frames;
  }
  
  public List<TryCatchBlockNode> getHandlers(int paramInt)
  {
    return handlers[paramInt];
  }
  
  protected void init(String paramString, MethodNode paramMethodNode)
    throws AnalyzerException
  {}
  
  protected Frame<V> newFrame(int paramInt1, int paramInt2)
  {
    return new Frame(paramInt1, paramInt2);
  }
  
  protected Frame<V> newFrame(Frame<? extends V> paramFrame)
  {
    return new Frame(paramFrame);
  }
  
  protected void newControlFlowEdge(int paramInt1, int paramInt2) {}
  
  protected boolean newControlFlowExceptionEdge(int paramInt1, int paramInt2)
  {
    return true;
  }
  
  protected boolean newControlFlowExceptionEdge(int paramInt, TryCatchBlockNode paramTryCatchBlockNode)
  {
    return newControlFlowExceptionEdge(paramInt, insns.indexOf(handler));
  }
  
  private void merge(int paramInt, Frame<V> paramFrame, Subroutine paramSubroutine)
    throws AnalyzerException
  {
    Frame localFrame = frames[paramInt];
    Subroutine localSubroutine = subroutines[paramInt];
    boolean bool;
    if (localFrame == null)
    {
      frames[paramInt] = newFrame(paramFrame);
      bool = true;
    }
    else
    {
      bool = localFrame.merge(paramFrame, interpreter);
    }
    if (localSubroutine == null)
    {
      if (paramSubroutine != null)
      {
        subroutines[paramInt] = paramSubroutine.copy();
        bool = true;
      }
    }
    else if (paramSubroutine != null) {
      bool |= localSubroutine.merge(paramSubroutine);
    }
    if ((bool) && (queued[paramInt] == 0))
    {
      queued[paramInt] = true;
      queue[(top++)] = paramInt;
    }
  }
  
  private void merge(int paramInt, Frame<V> paramFrame1, Frame<V> paramFrame2, Subroutine paramSubroutine, boolean[] paramArrayOfBoolean)
    throws AnalyzerException
  {
    Frame localFrame = frames[paramInt];
    Subroutine localSubroutine = subroutines[paramInt];
    paramFrame2.merge(paramFrame1, paramArrayOfBoolean);
    boolean bool;
    if (localFrame == null)
    {
      frames[paramInt] = newFrame(paramFrame2);
      bool = true;
    }
    else
    {
      bool = localFrame.merge(paramFrame2, interpreter);
    }
    if ((localSubroutine != null) && (paramSubroutine != null)) {
      bool |= localSubroutine.merge(paramSubroutine);
    }
    if ((bool) && (queued[paramInt] == 0))
    {
      queued[paramInt] = true;
      queue[(top++)] = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\Analyzer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */