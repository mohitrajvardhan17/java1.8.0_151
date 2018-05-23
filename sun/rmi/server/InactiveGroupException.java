package sun.rmi.server;

import java.rmi.activation.ActivationException;

public class InactiveGroupException
  extends ActivationException
{
  private static final long serialVersionUID = -7491041778450214975L;
  
  public InactiveGroupException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\InactiveGroupException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */