package sun.nio.ch;

import java.net.SocketOption;

class ExtendedSocketOption
{
  static final SocketOption<Boolean> SO_OOBINLINE = new SocketOption()
  {
    public String name()
    {
      return "SO_OOBINLINE";
    }
    
    public Class<Boolean> type()
    {
      return Boolean.class;
    }
    
    public String toString()
    {
      return name();
    }
  };
  
  private ExtendedSocketOption() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\ExtendedSocketOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */