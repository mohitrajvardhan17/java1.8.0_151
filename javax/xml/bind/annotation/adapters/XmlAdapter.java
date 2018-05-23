package javax.xml.bind.annotation.adapters;

public abstract class XmlAdapter<ValueType, BoundType>
{
  protected XmlAdapter() {}
  
  public abstract BoundType unmarshal(ValueType paramValueType)
    throws Exception;
  
  public abstract ValueType marshal(BoundType paramBoundType)
    throws Exception;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\adapters\XmlAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */