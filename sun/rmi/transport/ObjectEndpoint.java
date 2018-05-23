package sun.rmi.transport;

import java.rmi.server.ObjID;

class ObjectEndpoint
{
  private final ObjID id;
  private final Transport transport;
  
  ObjectEndpoint(ObjID paramObjID, Transport paramTransport)
  {
    if (paramObjID == null) {
      throw new NullPointerException();
    }
    assert ((paramTransport != null) || (paramObjID.equals(new ObjID(2))));
    id = paramObjID;
    transport = paramTransport;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ObjectEndpoint))
    {
      ObjectEndpoint localObjectEndpoint = (ObjectEndpoint)paramObject;
      return (id.equals(id)) && (transport == transport);
    }
    return false;
  }
  
  public int hashCode()
  {
    return id.hashCode() ^ (transport != null ? transport.hashCode() : 0);
  }
  
  public String toString()
  {
    return id.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\ObjectEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */