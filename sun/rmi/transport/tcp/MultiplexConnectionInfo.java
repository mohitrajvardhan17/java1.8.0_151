package sun.rmi.transport.tcp;

class MultiplexConnectionInfo
{
  int id;
  MultiplexInputStream in = null;
  MultiplexOutputStream out = null;
  boolean closed = false;
  
  MultiplexConnectionInfo(int paramInt)
  {
    id = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\tcp\MultiplexConnectionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */