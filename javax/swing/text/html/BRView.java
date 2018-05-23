package javax.swing.text.html;

import javax.swing.text.Element;

class BRView
  extends InlineView
{
  public BRView(Element paramElement)
  {
    super(paramElement);
  }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2)
  {
    if (paramInt == 0) {
      return 3000;
    }
    return super.getBreakWeight(paramInt, paramFloat1, paramFloat2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\BRView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */