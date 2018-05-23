package java.lang.instrument;

public final class ClassDefinition
{
  private final Class<?> mClass;
  private final byte[] mClassFile;
  
  public ClassDefinition(Class<?> paramClass, byte[] paramArrayOfByte)
  {
    if ((paramClass == null) || (paramArrayOfByte == null)) {
      throw new NullPointerException();
    }
    mClass = paramClass;
    mClassFile = paramArrayOfByte;
  }
  
  public Class<?> getDefinitionClass()
  {
    return mClass;
  }
  
  public byte[] getDefinitionClassFile()
  {
    return mClassFile;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\instrument\ClassDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */