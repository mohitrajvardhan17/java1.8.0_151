package java.io;

public class NotSerializableException
  extends ObjectStreamException
{
  private static final long serialVersionUID = 2906642554793891381L;
  
  public NotSerializableException(String paramString)
  {
    super(paramString);
  }
  
  public NotSerializableException() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\NotSerializableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */