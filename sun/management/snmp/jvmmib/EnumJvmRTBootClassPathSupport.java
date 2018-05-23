package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmRTBootClassPathSupport
  extends Enumerated
  implements Serializable
{
  static final long serialVersionUID = -5957542680437939894L;
  protected static Hashtable<Integer, String> intTable = new Hashtable();
  protected static Hashtable<String, Integer> stringTable = new Hashtable();
  
  public EnumJvmRTBootClassPathSupport(int paramInt)
    throws IllegalArgumentException
  {
    super(paramInt);
  }
  
  public EnumJvmRTBootClassPathSupport(Integer paramInteger)
    throws IllegalArgumentException
  {
    super(paramInteger);
  }
  
  public EnumJvmRTBootClassPathSupport()
    throws IllegalArgumentException
  {}
  
  public EnumJvmRTBootClassPathSupport(String paramString)
    throws IllegalArgumentException
  {
    super(paramString);
  }
  
  protected Hashtable<Integer, String> getIntTable()
  {
    return intTable;
  }
  
  protected Hashtable<String, Integer> getStringTable()
  {
    return stringTable;
  }
  
  static
  {
    intTable.put(new Integer(2), "supported");
    intTable.put(new Integer(1), "unsupported");
    stringTable.put("supported", new Integer(2));
    stringTable.put("unsupported", new Integer(1));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\EnumJvmRTBootClassPathSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */