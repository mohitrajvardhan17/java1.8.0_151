package jdk.internal.instrumentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

final class MethodCallInliner
  extends MethodVisitor
{
  private final String newClass;
  private final MethodNode inlineTarget;
  private final List<CatchBlock> blocks = new ArrayList();
  private boolean inlining;
  private final Logger logger;
  private final int maxLocals;
  
  public MethodCallInliner(int paramInt1, String paramString1, MethodVisitor paramMethodVisitor, MethodNode paramMethodNode, String paramString2, int paramInt2, Logger paramLogger)
  {
    super(327680, paramMethodVisitor);
    newClass = paramString2;
    inlineTarget = paramMethodNode;
    logger = paramLogger;
    maxLocals = paramInt2;
    paramLogger.trace("MethodCallInliner: targetMethod=" + paramString2 + "." + name + desc);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (!shouldBeInlined(paramString1, paramString2, paramString3))
    {
      mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    }
    logger.trace("Inlining call to " + paramString2 + paramString3);
    Label localLabel = new Label();
    inlining = true;
    inlineTarget.instructions.resetLabels();
    MethodInliningAdapter localMethodInliningAdapter = new MethodInliningAdapter(this, localLabel, paramInt == 184 ? 8 : 0, paramString3, maxLocals);
    inlineTarget.accept(localMethodInliningAdapter);
    logger.trace("Inlining done");
    inlining = false;
    super.visitLabel(localLabel);
  }
  
  private boolean shouldBeInlined(String paramString1, String paramString2, String paramString3)
  {
    return (inlineTarget.desc.equals(paramString3)) && (inlineTarget.name.equals(paramString2)) && (paramString1.equals(newClass.replace('.', '/')));
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    if (!inlining) {
      blocks.add(new CatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString));
    } else {
      super.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
    }
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    Iterator localIterator = blocks.iterator();
    while (localIterator.hasNext())
    {
      CatchBlock localCatchBlock = (CatchBlock)localIterator.next();
      super.visitTryCatchBlock(start, end, handler, type);
    }
    super.visitMaxs(paramInt1, paramInt2);
  }
  
  static final class CatchBlock
  {
    final Label start;
    final Label end;
    final Label handler;
    final String type;
    
    CatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
    {
      start = paramLabel1;
      end = paramLabel2;
      handler = paramLabel3;
      type = paramString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\instrumentation\MethodCallInliner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */