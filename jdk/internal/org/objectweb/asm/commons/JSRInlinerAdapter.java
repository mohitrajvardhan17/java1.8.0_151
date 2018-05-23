package jdk.internal.org.objectweb.asm.commons;

import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.InsnNode;
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode;
import jdk.internal.org.objectweb.asm.tree.LabelNode;
import jdk.internal.org.objectweb.asm.tree.LocalVariableNode;
import jdk.internal.org.objectweb.asm.tree.LookupSwitchInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.TableSwitchInsnNode;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;

public class JSRInlinerAdapter
  extends MethodNode
  implements Opcodes
{
  private static final boolean LOGGING = false;
  private final Map<LabelNode, BitSet> subroutineHeads = new HashMap();
  private final BitSet mainSubroutine = new BitSet();
  final BitSet dualCitizens = new BitSet();
  
  public JSRInlinerAdapter(MethodVisitor paramMethodVisitor, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    this(327680, paramMethodVisitor, paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    if (getClass() != JSRInlinerAdapter.class) {
      throw new IllegalStateException();
    }
  }
  
  protected JSRInlinerAdapter(int paramInt1, MethodVisitor paramMethodVisitor, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    super(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    mv = paramMethodVisitor;
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    super.visitJumpInsn(paramInt, paramLabel);
    LabelNode localLabelNode = instructions.getLast()).label;
    if ((paramInt == 168) && (!subroutineHeads.containsKey(localLabelNode))) {
      subroutineHeads.put(localLabelNode, new BitSet());
    }
  }
  
  public void visitEnd()
  {
    if (!subroutineHeads.isEmpty())
    {
      markSubroutines();
      emitCode();
    }
    if (mv != null) {
      accept(mv);
    }
  }
  
  private void markSubroutines()
  {
    BitSet localBitSet1 = new BitSet();
    markSubroutineWalk(mainSubroutine, 0, localBitSet1);
    Iterator localIterator = subroutineHeads.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      LabelNode localLabelNode = (LabelNode)localEntry.getKey();
      BitSet localBitSet2 = (BitSet)localEntry.getValue();
      int i = instructions.indexOf(localLabelNode);
      markSubroutineWalk(localBitSet2, i, localBitSet1);
    }
  }
  
  private void markSubroutineWalk(BitSet paramBitSet1, int paramInt, BitSet paramBitSet2)
  {
    markSubroutineWalkDFS(paramBitSet1, paramInt, paramBitSet2);
    int i = 1;
    while (i != 0)
    {
      i = 0;
      Iterator localIterator = tryCatchBlocks.iterator();
      while (localIterator.hasNext())
      {
        TryCatchBlockNode localTryCatchBlockNode = (TryCatchBlockNode)localIterator.next();
        int j = instructions.indexOf(handler);
        if (!paramBitSet1.get(j))
        {
          int k = instructions.indexOf(start);
          int m = instructions.indexOf(end);
          int n = paramBitSet1.nextSetBit(k);
          if ((n != -1) && (n < m))
          {
            markSubroutineWalkDFS(paramBitSet1, j, paramBitSet2);
            i = 1;
          }
        }
      }
    }
  }
  
  private void markSubroutineWalkDFS(BitSet paramBitSet1, int paramInt, BitSet paramBitSet2)
  {
    for (;;)
    {
      AbstractInsnNode localAbstractInsnNode = instructions.get(paramInt);
      if (paramBitSet1.get(paramInt)) {
        return;
      }
      paramBitSet1.set(paramInt);
      if (paramBitSet2.get(paramInt)) {
        dualCitizens.set(paramInt);
      }
      paramBitSet2.set(paramInt);
      Object localObject;
      int i;
      if ((localAbstractInsnNode.getType() == 7) && (localAbstractInsnNode.getOpcode() != 168))
      {
        localObject = (JumpInsnNode)localAbstractInsnNode;
        i = instructions.indexOf(label);
        markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
      }
      int j;
      LabelNode localLabelNode;
      if (localAbstractInsnNode.getType() == 11)
      {
        localObject = (TableSwitchInsnNode)localAbstractInsnNode;
        i = instructions.indexOf(dflt);
        markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
        for (j = labels.size() - 1; j >= 0; j--)
        {
          localLabelNode = (LabelNode)labels.get(j);
          i = instructions.indexOf(localLabelNode);
          markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
        }
      }
      if (localAbstractInsnNode.getType() == 12)
      {
        localObject = (LookupSwitchInsnNode)localAbstractInsnNode;
        i = instructions.indexOf(dflt);
        markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
        for (j = labels.size() - 1; j >= 0; j--)
        {
          localLabelNode = (LabelNode)labels.get(j);
          i = instructions.indexOf(localLabelNode);
          markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
        }
      }
      switch (instructions.get(paramInt).getOpcode())
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
      if (paramInt >= instructions.size()) {
        return;
      }
    }
  }
  
  private void emitCode()
  {
    LinkedList localLinkedList = new LinkedList();
    localLinkedList.add(new Instantiation(null, mainSubroutine));
    InsnList localInsnList = new InsnList();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    while (!localLinkedList.isEmpty())
    {
      Instantiation localInstantiation = (Instantiation)localLinkedList.removeFirst();
      emitSubroutine(localInstantiation, localLinkedList, localInsnList, localArrayList1, localArrayList2);
    }
    instructions = localInsnList;
    tryCatchBlocks = localArrayList1;
    localVariables = localArrayList2;
  }
  
  private void emitSubroutine(Instantiation paramInstantiation, List<Instantiation> paramList, InsnList paramInsnList, List<TryCatchBlockNode> paramList1, List<LocalVariableNode> paramList2)
  {
    Object localObject1 = null;
    int i = 0;
    int j = instructions.size();
    Object localObject3;
    Object localObject4;
    LabelNode localLabelNode1;
    while (i < j)
    {
      localObject3 = instructions.get(i);
      localObject4 = paramInstantiation.findOwner(i);
      Object localObject5;
      if (((AbstractInsnNode)localObject3).getType() == 8)
      {
        localLabelNode1 = (LabelNode)localObject3;
        localObject5 = paramInstantiation.rangeLabel(localLabelNode1);
        if (localObject5 != localObject1)
        {
          paramInsnList.add((AbstractInsnNode)localObject5);
          localObject1 = localObject5;
        }
      }
      else if (localObject4 == paramInstantiation)
      {
        if (((AbstractInsnNode)localObject3).getOpcode() == 169)
        {
          localLabelNode1 = null;
          for (localObject5 = paramInstantiation; localObject5 != null; localObject5 = previous) {
            if (subroutine.get(i)) {
              localLabelNode1 = returnLabel;
            }
          }
          if (localLabelNode1 == null) {
            throw new RuntimeException("Instruction #" + i + " is a RET not owned by any subroutine");
          }
          paramInsnList.add(new JumpInsnNode(167, localLabelNode1));
        }
        else if (((AbstractInsnNode)localObject3).getOpcode() == 168)
        {
          localLabelNode1 = label;
          localObject5 = (BitSet)subroutineHeads.get(localLabelNode1);
          Instantiation localInstantiation = new Instantiation(paramInstantiation, (BitSet)localObject5);
          LabelNode localLabelNode2 = localInstantiation.gotoLabel(localLabelNode1);
          paramInsnList.add(new InsnNode(1));
          paramInsnList.add(new JumpInsnNode(167, localLabelNode2));
          paramInsnList.add(returnLabel);
          paramList.add(localInstantiation);
        }
        else
        {
          paramInsnList.add(((AbstractInsnNode)localObject3).clone(paramInstantiation));
        }
      }
      i++;
    }
    Iterator localIterator = tryCatchBlocks.iterator();
    Object localObject2;
    while (localIterator.hasNext())
    {
      localObject2 = (TryCatchBlockNode)localIterator.next();
      localObject3 = paramInstantiation.rangeLabel(start);
      localObject4 = paramInstantiation.rangeLabel(end);
      if (localObject3 != localObject4)
      {
        localLabelNode1 = paramInstantiation.gotoLabel(handler);
        if ((localObject3 == null) || (localObject4 == null) || (localLabelNode1 == null)) {
          throw new RuntimeException("Internal error!");
        }
        paramList1.add(new TryCatchBlockNode((LabelNode)localObject3, (LabelNode)localObject4, localLabelNode1, type));
      }
    }
    localIterator = localVariables.iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (LocalVariableNode)localIterator.next();
      localObject3 = paramInstantiation.rangeLabel(start);
      localObject4 = paramInstantiation.rangeLabel(end);
      if (localObject3 != localObject4) {
        paramList2.add(new LocalVariableNode(name, desc, signature, (LabelNode)localObject3, (LabelNode)localObject4, index));
      }
    }
  }
  
  private static void log(String paramString)
  {
    System.err.println(paramString);
  }
  
  private class Instantiation
    extends AbstractMap<LabelNode, LabelNode>
  {
    final Instantiation previous;
    public final BitSet subroutine;
    public final Map<LabelNode, LabelNode> rangeTable = new HashMap();
    public final LabelNode returnLabel;
    
    Instantiation(Instantiation paramInstantiation, BitSet paramBitSet)
    {
      previous = paramInstantiation;
      subroutine = paramBitSet;
      for (Object localObject = paramInstantiation; localObject != null; localObject = previous) {
        if (subroutine == paramBitSet) {
          throw new RuntimeException("Recursive invocation of " + paramBitSet);
        }
      }
      if (paramInstantiation != null) {
        returnLabel = new LabelNode();
      } else {
        returnLabel = null;
      }
      localObject = null;
      int i = 0;
      int j = instructions.size();
      while (i < j)
      {
        AbstractInsnNode localAbstractInsnNode = instructions.get(i);
        if (localAbstractInsnNode.getType() == 8)
        {
          LabelNode localLabelNode = (LabelNode)localAbstractInsnNode;
          if (localObject == null) {
            localObject = new LabelNode();
          }
          rangeTable.put(localLabelNode, localObject);
        }
        else if (findOwner(i) == this)
        {
          localObject = null;
        }
        i++;
      }
    }
    
    public Instantiation findOwner(int paramInt)
    {
      if (!subroutine.get(paramInt)) {
        return null;
      }
      if (!dualCitizens.get(paramInt)) {
        return this;
      }
      Object localObject = this;
      for (Instantiation localInstantiation = previous; localInstantiation != null; localInstantiation = previous) {
        if (subroutine.get(paramInt)) {
          localObject = localInstantiation;
        }
      }
      return (Instantiation)localObject;
    }
    
    public LabelNode gotoLabel(LabelNode paramLabelNode)
    {
      Instantiation localInstantiation = findOwner(instructions.indexOf(paramLabelNode));
      return (LabelNode)rangeTable.get(paramLabelNode);
    }
    
    public LabelNode rangeLabel(LabelNode paramLabelNode)
    {
      return (LabelNode)rangeTable.get(paramLabelNode);
    }
    
    public Set<Map.Entry<LabelNode, LabelNode>> entrySet()
    {
      return null;
    }
    
    public LabelNode get(Object paramObject)
    {
      return gotoLabel((LabelNode)paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\JSRInlinerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */