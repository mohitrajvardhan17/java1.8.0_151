package sun.reflect;

class ByteVectorFactory
{
  ByteVectorFactory() {}
  
  static ByteVector create()
  {
    return new ByteVectorImpl();
  }
  
  static ByteVector create(int paramInt)
  {
    return new ByteVectorImpl(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\ByteVectorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */