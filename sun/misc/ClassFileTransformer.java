package sun.misc;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class ClassFileTransformer
{
  private static final List<ClassFileTransformer> transformers = new ArrayList();
  
  public ClassFileTransformer() {}
  
  public static void add(ClassFileTransformer paramClassFileTransformer)
  {
    synchronized (transformers)
    {
      transformers.add(paramClassFileTransformer);
    }
  }
  
  public static ClassFileTransformer[] getTransformers()
  {
    synchronized (transformers)
    {
      ClassFileTransformer[] arrayOfClassFileTransformer = new ClassFileTransformer[transformers.size()];
      return (ClassFileTransformer[])transformers.toArray(arrayOfClassFileTransformer);
    }
  }
  
  public abstract byte[] transform(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ClassFormatError;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\ClassFileTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */