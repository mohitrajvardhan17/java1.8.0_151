package org.omg.CORBA;

public abstract interface CustomMarshal
{
  public abstract void marshal(DataOutputStream paramDataOutputStream);
  
  public abstract void unmarshal(DataInputStream paramDataInputStream);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\CustomMarshal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */