package com.sun.jmx.snmp;

public class SnmpOpaque
  extends SnmpString
{
  private static final long serialVersionUID = 380952213936036664L;
  static final String name = "Opaque";
  
  public SnmpOpaque(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }
  
  public SnmpOpaque(Byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }
  
  public SnmpOpaque(String paramString)
  {
    super(paramString);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < value.length; i++)
    {
      int j = value[i];
      int k = j >= 0 ? j : j + 256;
      localStringBuffer.append(Character.forDigit(k / 16, 16));
      localStringBuffer.append(Character.forDigit(k % 16, 16));
    }
    return localStringBuffer.toString();
  }
  
  public final String getTypeName()
  {
    return "Opaque";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpOpaque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */