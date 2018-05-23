package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class SliderTrackArrowShapeState
  extends State
{
  SliderTrackArrowShapeState()
  {
    super("ArrowShape");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return paramJComponent.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\SliderTrackArrowShapeState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */