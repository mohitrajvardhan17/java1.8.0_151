package java.io;

public abstract interface Externalizable
  extends Serializable
{
  public abstract void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException;
  
  public abstract void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\Externalizable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */