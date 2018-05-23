package javax.swing.text.rtf;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

class RTFAttributes
{
  static RTFAttribute[] attributes;
  
  RTFAttributes() {}
  
  static Dictionary<String, RTFAttribute> attributesByKeyword()
  {
    Hashtable localHashtable = new Hashtable(attributes.length);
    for (RTFAttribute localRTFAttribute : attributes) {
      localHashtable.put(localRTFAttribute.rtfName(), localRTFAttribute);
    }
    return localHashtable;
  }
  
  static
  {
    Vector localVector = new Vector();
    int i = 0;
    int j = 1;
    int k = 2;
    int m = 3;
    int n = 4;
    Boolean localBoolean1 = Boolean.valueOf(true);
    Boolean localBoolean2 = Boolean.valueOf(false);
    localVector.addElement(new BooleanAttribute(i, StyleConstants.Italic, "i"));
    localVector.addElement(new BooleanAttribute(i, StyleConstants.Bold, "b"));
    localVector.addElement(new BooleanAttribute(i, StyleConstants.Underline, "ul"));
    localVector.addElement(NumericAttribute.NewTwips(j, StyleConstants.LeftIndent, "li", 0.0F, 0));
    localVector.addElement(NumericAttribute.NewTwips(j, StyleConstants.RightIndent, "ri", 0.0F, 0));
    localVector.addElement(NumericAttribute.NewTwips(j, StyleConstants.FirstLineIndent, "fi", 0.0F, 0));
    localVector.addElement(new AssertiveAttribute(j, StyleConstants.Alignment, "ql", 0));
    localVector.addElement(new AssertiveAttribute(j, StyleConstants.Alignment, "qr", 2));
    localVector.addElement(new AssertiveAttribute(j, StyleConstants.Alignment, "qc", 1));
    localVector.addElement(new AssertiveAttribute(j, StyleConstants.Alignment, "qj", 3));
    localVector.addElement(NumericAttribute.NewTwips(j, StyleConstants.SpaceAbove, "sa", 0));
    localVector.addElement(NumericAttribute.NewTwips(j, StyleConstants.SpaceBelow, "sb", 0));
    localVector.addElement(new AssertiveAttribute(n, "tab_alignment", "tqr", 1));
    localVector.addElement(new AssertiveAttribute(n, "tab_alignment", "tqc", 2));
    localVector.addElement(new AssertiveAttribute(n, "tab_alignment", "tqdec", 4));
    localVector.addElement(new AssertiveAttribute(n, "tab_leader", "tldot", 1));
    localVector.addElement(new AssertiveAttribute(n, "tab_leader", "tlhyph", 2));
    localVector.addElement(new AssertiveAttribute(n, "tab_leader", "tlul", 3));
    localVector.addElement(new AssertiveAttribute(n, "tab_leader", "tlth", 4));
    localVector.addElement(new AssertiveAttribute(n, "tab_leader", "tleq", 5));
    localVector.addElement(new BooleanAttribute(i, "caps", "caps"));
    localVector.addElement(new BooleanAttribute(i, "outl", "outl"));
    localVector.addElement(new BooleanAttribute(i, "scaps", "scaps"));
    localVector.addElement(new BooleanAttribute(i, "shad", "shad"));
    localVector.addElement(new BooleanAttribute(i, "v", "v"));
    localVector.addElement(new BooleanAttribute(i, "strike", "strike"));
    localVector.addElement(new BooleanAttribute(i, "deleted", "deleted"));
    localVector.addElement(new AssertiveAttribute(m, "saveformat", "defformat", "RTF"));
    localVector.addElement(new AssertiveAttribute(m, "landscape", "landscape"));
    localVector.addElement(NumericAttribute.NewTwips(m, "paperw", "paperw", 12240));
    localVector.addElement(NumericAttribute.NewTwips(m, "paperh", "paperh", 15840));
    localVector.addElement(NumericAttribute.NewTwips(m, "margl", "margl", 1800));
    localVector.addElement(NumericAttribute.NewTwips(m, "margr", "margr", 1800));
    localVector.addElement(NumericAttribute.NewTwips(m, "margt", "margt", 1440));
    localVector.addElement(NumericAttribute.NewTwips(m, "margb", "margb", 1440));
    localVector.addElement(NumericAttribute.NewTwips(m, "gutter", "gutter", 0));
    localVector.addElement(new AssertiveAttribute(j, "widowctrl", "nowidctlpar", localBoolean2));
    localVector.addElement(new AssertiveAttribute(j, "widowctrl", "widctlpar", localBoolean1));
    localVector.addElement(new AssertiveAttribute(m, "widowctrl", "widowctrl", localBoolean1));
    RTFAttribute[] arrayOfRTFAttribute = new RTFAttribute[localVector.size()];
    localVector.copyInto(arrayOfRTFAttribute);
    attributes = arrayOfRTFAttribute;
  }
  
  static class AssertiveAttribute
    extends RTFAttributes.GenericAttribute
    implements RTFAttribute
  {
    Object swingValue;
    
    public AssertiveAttribute(int paramInt, Object paramObject, String paramString)
    {
      super(paramObject, paramString);
      swingValue = Boolean.valueOf(true);
    }
    
    public AssertiveAttribute(int paramInt, Object paramObject1, String paramString, Object paramObject2)
    {
      super(paramObject1, paramString);
      swingValue = paramObject2;
    }
    
    public AssertiveAttribute(int paramInt1, Object paramObject, String paramString, int paramInt2)
    {
      super(paramObject, paramString);
      swingValue = Integer.valueOf(paramInt2);
    }
    
    public boolean set(MutableAttributeSet paramMutableAttributeSet)
    {
      if (swingValue == null) {
        paramMutableAttributeSet.removeAttribute(swingName);
      } else {
        paramMutableAttributeSet.addAttribute(swingName, swingValue);
      }
      return true;
    }
    
    public boolean set(MutableAttributeSet paramMutableAttributeSet, int paramInt)
    {
      return false;
    }
    
    public boolean setDefault(MutableAttributeSet paramMutableAttributeSet)
    {
      paramMutableAttributeSet.removeAttribute(swingName);
      return true;
    }
    
    public boolean writeValue(Object paramObject, RTFGenerator paramRTFGenerator, boolean paramBoolean)
      throws IOException
    {
      if (paramObject == null) {
        return !paramBoolean;
      }
      if (paramObject.equals(swingValue))
      {
        paramRTFGenerator.writeControlWord(rtfName);
        return true;
      }
      return !paramBoolean;
    }
  }
  
  static class BooleanAttribute
    extends RTFAttributes.GenericAttribute
    implements RTFAttribute
  {
    boolean rtfDefault;
    boolean swingDefault;
    protected static final Boolean True = Boolean.valueOf(true);
    protected static final Boolean False = Boolean.valueOf(false);
    
    public BooleanAttribute(int paramInt, Object paramObject, String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      super(paramObject, paramString);
      swingDefault = paramBoolean1;
      rtfDefault = paramBoolean2;
    }
    
    public BooleanAttribute(int paramInt, Object paramObject, String paramString)
    {
      super(paramObject, paramString);
      swingDefault = false;
      rtfDefault = false;
    }
    
    public boolean set(MutableAttributeSet paramMutableAttributeSet)
    {
      paramMutableAttributeSet.addAttribute(swingName, True);
      return true;
    }
    
    public boolean set(MutableAttributeSet paramMutableAttributeSet, int paramInt)
    {
      Boolean localBoolean = paramInt != 0 ? True : False;
      paramMutableAttributeSet.addAttribute(swingName, localBoolean);
      return true;
    }
    
    public boolean setDefault(MutableAttributeSet paramMutableAttributeSet)
    {
      if ((swingDefault != rtfDefault) || (paramMutableAttributeSet.getAttribute(swingName) != null)) {
        paramMutableAttributeSet.addAttribute(swingName, Boolean.valueOf(rtfDefault));
      }
      return true;
    }
    
    public boolean writeValue(Object paramObject, RTFGenerator paramRTFGenerator, boolean paramBoolean)
      throws IOException
    {
      Boolean localBoolean;
      if (paramObject == null) {
        localBoolean = Boolean.valueOf(swingDefault);
      } else {
        localBoolean = (Boolean)paramObject;
      }
      if ((paramBoolean) || (localBoolean.booleanValue() != rtfDefault)) {
        if (localBoolean.booleanValue()) {
          paramRTFGenerator.writeControlWord(rtfName);
        } else {
          paramRTFGenerator.writeControlWord(rtfName, 0);
        }
      }
      return true;
    }
  }
  
  static abstract class GenericAttribute
  {
    int domain;
    Object swingName;
    String rtfName;
    
    protected GenericAttribute(int paramInt, Object paramObject, String paramString)
    {
      domain = paramInt;
      swingName = paramObject;
      rtfName = paramString;
    }
    
    public int domain()
    {
      return domain;
    }
    
    public Object swingName()
    {
      return swingName;
    }
    
    public String rtfName()
    {
      return rtfName;
    }
    
    abstract boolean set(MutableAttributeSet paramMutableAttributeSet);
    
    abstract boolean set(MutableAttributeSet paramMutableAttributeSet, int paramInt);
    
    abstract boolean setDefault(MutableAttributeSet paramMutableAttributeSet);
    
    public boolean write(AttributeSet paramAttributeSet, RTFGenerator paramRTFGenerator, boolean paramBoolean)
      throws IOException
    {
      return writeValue(paramAttributeSet.getAttribute(swingName), paramRTFGenerator, paramBoolean);
    }
    
    public boolean writeValue(Object paramObject, RTFGenerator paramRTFGenerator, boolean paramBoolean)
      throws IOException
    {
      return false;
    }
  }
  
  static class NumericAttribute
    extends RTFAttributes.GenericAttribute
    implements RTFAttribute
  {
    int rtfDefault;
    Number swingDefault;
    float scale;
    
    protected NumericAttribute(int paramInt, Object paramObject, String paramString)
    {
      super(paramObject, paramString);
      rtfDefault = 0;
      swingDefault = null;
      scale = 1.0F;
    }
    
    public NumericAttribute(int paramInt1, Object paramObject, String paramString, int paramInt2, int paramInt3)
    {
      this(paramInt1, paramObject, paramString, Integer.valueOf(paramInt2), paramInt3, 1.0F);
    }
    
    public NumericAttribute(int paramInt1, Object paramObject, String paramString, Number paramNumber, int paramInt2, float paramFloat)
    {
      super(paramObject, paramString);
      swingDefault = paramNumber;
      rtfDefault = paramInt2;
      scale = paramFloat;
    }
    
    public static NumericAttribute NewTwips(int paramInt1, Object paramObject, String paramString, float paramFloat, int paramInt2)
    {
      return new NumericAttribute(paramInt1, paramObject, paramString, new Float(paramFloat), paramInt2, 20.0F);
    }
    
    public static NumericAttribute NewTwips(int paramInt1, Object paramObject, String paramString, int paramInt2)
    {
      return new NumericAttribute(paramInt1, paramObject, paramString, null, paramInt2, 20.0F);
    }
    
    public boolean set(MutableAttributeSet paramMutableAttributeSet)
    {
      return false;
    }
    
    public boolean set(MutableAttributeSet paramMutableAttributeSet, int paramInt)
    {
      Object localObject;
      if (scale == 1.0F) {
        localObject = Integer.valueOf(paramInt);
      } else {
        localObject = new Float(paramInt / scale);
      }
      paramMutableAttributeSet.addAttribute(swingName, localObject);
      return true;
    }
    
    public boolean setDefault(MutableAttributeSet paramMutableAttributeSet)
    {
      Number localNumber = (Number)paramMutableAttributeSet.getAttribute(swingName);
      if (localNumber == null) {
        localNumber = swingDefault;
      }
      if ((localNumber != null) && (((scale == 1.0F) && (localNumber.intValue() == rtfDefault)) || (Math.round(localNumber.floatValue() * scale) == rtfDefault))) {
        return true;
      }
      set(paramMutableAttributeSet, rtfDefault);
      return true;
    }
    
    public boolean writeValue(Object paramObject, RTFGenerator paramRTFGenerator, boolean paramBoolean)
      throws IOException
    {
      Number localNumber = (Number)paramObject;
      if (localNumber == null) {
        localNumber = swingDefault;
      }
      if (localNumber == null) {
        return true;
      }
      int i = Math.round(localNumber.floatValue() * scale);
      if ((paramBoolean) || (i != rtfDefault)) {
        paramRTFGenerator.writeControlWord(rtfName, i);
      }
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\rtf\RTFAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */