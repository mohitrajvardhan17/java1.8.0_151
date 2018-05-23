package javax.sound.sampled;

import java.util.EventObject;

public class LineEvent
  extends EventObject
{
  private final Type type;
  private final long position;
  
  public LineEvent(Line paramLine, Type paramType, long paramLong)
  {
    super(paramLine);
    type = paramType;
    position = paramLong;
  }
  
  public final Line getLine()
  {
    return (Line)getSource();
  }
  
  public final Type getType()
  {
    return type;
  }
  
  public final long getFramePosition()
  {
    return position;
  }
  
  public String toString()
  {
    String str1 = "";
    if (type != null) {
      str1 = type.toString() + " ";
    }
    String str2;
    if (getLine() == null) {
      str2 = "null";
    } else {
      str2 = getLine().toString();
    }
    return new String(str1 + "event from line " + str2);
  }
  
  public static class Type
  {
    private String name;
    public static final Type OPEN = new Type("Open");
    public static final Type CLOSE = new Type("Close");
    public static final Type START = new Type("Start");
    public static final Type STOP = new Type("Stop");
    
    protected Type(String paramString)
    {
      name = paramString;
    }
    
    public final boolean equals(Object paramObject)
    {
      return super.equals(paramObject);
    }
    
    public final int hashCode()
    {
      return super.hashCode();
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\LineEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */