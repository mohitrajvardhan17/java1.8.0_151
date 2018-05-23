package java.awt;

import java.io.Serializable;

class FocusManager
  implements Serializable
{
  Container focusRoot;
  Component focusOwner;
  static final long serialVersionUID = 2491878825643557906L;
  
  FocusManager() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\FocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */