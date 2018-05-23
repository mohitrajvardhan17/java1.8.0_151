package sun.security.tools.policytool;

import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;

class TaggedList
  extends JList
{
  private static final long serialVersionUID = -5676238110427785853L;
  private List<Object> data = new LinkedList();
  
  public TaggedList(int paramInt, boolean paramBoolean)
  {
    super(new DefaultListModel());
    setVisibleRowCount(paramInt);
    setSelectionMode(paramBoolean ? 2 : 0);
  }
  
  public Object getObject(int paramInt)
  {
    return data.get(paramInt);
  }
  
  public void addTaggedItem(String paramString, Object paramObject)
  {
    ((DefaultListModel)getModel()).addElement(paramString);
    data.add(paramObject);
  }
  
  public void replaceTaggedItem(String paramString, Object paramObject, int paramInt)
  {
    ((DefaultListModel)getModel()).set(paramInt, paramString);
    data.set(paramInt, paramObject);
  }
  
  public void removeTaggedItem(int paramInt)
  {
    ((DefaultListModel)getModel()).remove(paramInt);
    data.remove(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\TaggedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */