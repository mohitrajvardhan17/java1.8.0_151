package jdk.internal.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;

public class TryCatchBlockSorter
  extends MethodNode
{
  public TryCatchBlockSorter(MethodVisitor paramMethodVisitor, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    this(327680, paramMethodVisitor, paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  protected TryCatchBlockSorter(int paramInt1, MethodVisitor paramMethodVisitor, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    super(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    mv = paramMethodVisitor;
  }
  
  public void visitEnd()
  {
    Comparator local1 = new Comparator()
    {
      public int compare(TryCatchBlockNode paramAnonymousTryCatchBlockNode1, TryCatchBlockNode paramAnonymousTryCatchBlockNode2)
      {
        int i = blockLength(paramAnonymousTryCatchBlockNode1);
        int j = blockLength(paramAnonymousTryCatchBlockNode2);
        return i - j;
      }
      
      private int blockLength(TryCatchBlockNode paramAnonymousTryCatchBlockNode)
      {
        int i = instructions.indexOf(start);
        int j = instructions.indexOf(end);
        return j - i;
      }
    };
    Collections.sort(tryCatchBlocks, local1);
    for (int i = 0; i < tryCatchBlocks.size(); i++) {
      ((TryCatchBlockNode)tryCatchBlocks.get(i)).updateIndex(i);
    }
    if (mv != null) {
      accept(mv);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\TryCatchBlockSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */