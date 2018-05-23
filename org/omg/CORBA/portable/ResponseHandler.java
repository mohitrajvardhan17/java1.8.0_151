package org.omg.CORBA.portable;

public abstract interface ResponseHandler
{
  public abstract OutputStream createReply();
  
  public abstract OutputStream createExceptionReply();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\portable\ResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */