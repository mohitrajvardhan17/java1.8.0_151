package jdk.internal.org.objectweb.asm.tree;

import jdk.internal.org.objectweb.asm.TypePath;

public class TypeAnnotationNode
  extends AnnotationNode
{
  public int typeRef;
  public TypePath typePath;
  
  public TypeAnnotationNode(int paramInt, TypePath paramTypePath, String paramString)
  {
    this(327680, paramInt, paramTypePath, paramString);
    if (getClass() != TypeAnnotationNode.class) {
      throw new IllegalStateException();
    }
  }
  
  public TypeAnnotationNode(int paramInt1, int paramInt2, TypePath paramTypePath, String paramString)
  {
    super(paramInt1, paramString);
    typeRef = paramInt2;
    typePath = paramTypePath;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\TypeAnnotationNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */