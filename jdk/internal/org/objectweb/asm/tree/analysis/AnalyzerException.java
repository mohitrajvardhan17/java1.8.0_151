package jdk.internal.org.objectweb.asm.tree.analysis;

import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

public class AnalyzerException
  extends Exception
{
  public final AbstractInsnNode node;
  
  public AnalyzerException(AbstractInsnNode paramAbstractInsnNode, String paramString)
  {
    super(paramString);
    node = paramAbstractInsnNode;
  }
  
  public AnalyzerException(AbstractInsnNode paramAbstractInsnNode, String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    node = paramAbstractInsnNode;
  }
  
  public AnalyzerException(AbstractInsnNode paramAbstractInsnNode, String paramString, Object paramObject, Value paramValue)
  {
    super((paramString == null ? "Expected " : new StringBuilder().append(paramString).append(": expected ").toString()) + paramObject + ", but found " + paramValue);
    node = paramAbstractInsnNode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\AnalyzerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */