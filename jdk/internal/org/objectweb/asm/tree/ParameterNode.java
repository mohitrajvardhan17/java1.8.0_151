package jdk.internal.org.objectweb.asm.tree;

import jdk.internal.org.objectweb.asm.MethodVisitor;

public class ParameterNode
{
  public String name;
  public int access;
  
  public ParameterNode(String paramString, int paramInt)
  {
    name = paramString;
    access = paramInt;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitParameter(name, access);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\ParameterNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */