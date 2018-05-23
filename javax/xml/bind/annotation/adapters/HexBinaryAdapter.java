package javax.xml.bind.annotation.adapters;

import javax.xml.bind.DatatypeConverter;

public final class HexBinaryAdapter
  extends XmlAdapter<String, byte[]>
{
  public HexBinaryAdapter() {}
  
  public byte[] unmarshal(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return DatatypeConverter.parseHexBinary(paramString);
  }
  
  public String marshal(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    return DatatypeConverter.printHexBinary(paramArrayOfByte);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\adapters\HexBinaryAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */