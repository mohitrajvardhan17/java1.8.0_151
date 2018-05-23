package javax.swing.plaf.synth;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import sun.swing.DefaultLookup;

class SynthDefaultLookup
  extends DefaultLookup
{
  SynthDefaultLookup() {}
  
  public Object getDefault(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString)
  {
    if (!(paramComponentUI instanceof SynthUI))
    {
      localObject1 = super.getDefault(paramJComponent, paramComponentUI, paramString);
      return localObject1;
    }
    Object localObject1 = ((SynthUI)paramComponentUI).getContext(paramJComponent);
    Object localObject2 = ((SynthContext)localObject1).getStyle().get((SynthContext)localObject1, paramString);
    ((SynthContext)localObject1).dispose();
    return localObject2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthDefaultLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */