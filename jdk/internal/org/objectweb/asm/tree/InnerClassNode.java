package jdk.internal.org.objectweb.asm.tree;

import jdk.internal.org.objectweb.asm.ClassVisitor;

public class InnerClassNode
{
  public String name;
  public String outerName;
  public String innerName;
  public int access;
  
  public InnerClassNode(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    name = paramString1;
    outerName = paramString2;
    innerName = paramString3;
    access = paramInt;
  }
  
  public void accept(ClassVisitor paramClassVisitor)
  {
    paramClassVisitor.visitInnerClass(name, outerName, innerName, access);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\InnerClassNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */