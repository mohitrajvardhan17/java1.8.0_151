package javax.swing.text.html;

import java.util.StringTokenizer;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;

class FrameSetView
  extends BoxView
{
  String[] children = null;
  int[] percentChildren;
  int[] absoluteChildren;
  int[] relativeChildren;
  int percentTotals;
  int absoluteTotals;
  int relativeTotals;
  
  public FrameSetView(Element paramElement, int paramInt)
  {
    super(paramElement, paramInt);
  }
  
  private String[] parseRowColSpec(HTML.Attribute paramAttribute)
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    String str = "*";
    if ((localAttributeSet != null) && (localAttributeSet.getAttribute(paramAttribute) != null)) {
      str = (String)localAttributeSet.getAttribute(paramAttribute);
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(str, ",");
    int i = localStringTokenizer.countTokens();
    int j = getViewCount();
    String[] arrayOfString = new String[Math.max(i, j)];
    for (int k = 0; k < i; k++)
    {
      arrayOfString[k] = localStringTokenizer.nextToken().trim();
      if (arrayOfString[k].equals("100%")) {
        arrayOfString[k] = "*";
      }
    }
    while (k < arrayOfString.length)
    {
      arrayOfString[k] = "*";
      k++;
    }
    return arrayOfString;
  }
  
  private void init()
  {
    if (getAxis() == 1) {
      children = parseRowColSpec(HTML.Attribute.ROWS);
    } else {
      children = parseRowColSpec(HTML.Attribute.COLS);
    }
    percentChildren = new int[children.length];
    relativeChildren = new int[children.length];
    absoluteChildren = new int[children.length];
    for (int i = 0; i < children.length; i++)
    {
      percentChildren[i] = -1;
      relativeChildren[i] = -1;
      absoluteChildren[i] = -1;
      if (children[i].endsWith("*"))
      {
        if (children[i].length() > 1)
        {
          relativeChildren[i] = Integer.parseInt(children[i].substring(0, children[i].length() - 1));
          relativeTotals += relativeChildren[i];
        }
        else
        {
          relativeChildren[i] = 1;
          relativeTotals += 1;
        }
      }
      else if (children[i].indexOf('%') != -1)
      {
        percentChildren[i] = parseDigits(children[i]);
        percentTotals += percentChildren[i];
      }
      else
      {
        absoluteChildren[i] = Integer.parseInt(children[i]);
      }
    }
    if (percentTotals > 100)
    {
      for (i = 0; i < percentChildren.length; i++) {
        if (percentChildren[i] > 0) {
          percentChildren[i] = (percentChildren[i] * 100 / percentTotals);
        }
      }
      percentTotals = 100;
    }
  }
  
  protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (children == null) {
      init();
    }
    SizeRequirements.calculateTiledPositions(paramInt1, null, getChildRequests(paramInt1, paramInt2), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  protected SizeRequirements[] getChildRequests(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = new int[children.length];
    spread(paramInt1, arrayOfInt);
    int i = getViewCount();
    SizeRequirements[] arrayOfSizeRequirements = new SizeRequirements[i];
    int j = 0;
    int k = 0;
    while (j < i)
    {
      View localView = getView(j);
      if (((localView instanceof FrameView)) || ((localView instanceof FrameSetView)))
      {
        arrayOfSizeRequirements[j] = new SizeRequirements((int)localView.getMinimumSpan(paramInt2), arrayOfInt[k], (int)localView.getMaximumSpan(paramInt2), 0.5F);
        k++;
      }
      else
      {
        int m = (int)localView.getMinimumSpan(paramInt2);
        int n = (int)localView.getPreferredSpan(paramInt2);
        int i1 = (int)localView.getMaximumSpan(paramInt2);
        float f = localView.getAlignment(paramInt2);
        arrayOfSizeRequirements[j] = new SizeRequirements(m, n, i1, f);
      }
      j++;
    }
    return arrayOfSizeRequirements;
  }
  
  private void spread(int paramInt, int[] paramArrayOfInt)
  {
    if (paramInt == 0) {
      return;
    }
    int i = 0;
    int j = paramInt;
    for (int k = 0; k < paramArrayOfInt.length; k++) {
      if (absoluteChildren[k] > 0)
      {
        paramArrayOfInt[k] = absoluteChildren[k];
        j -= paramArrayOfInt[k];
      }
    }
    i = j;
    for (k = 0; k < paramArrayOfInt.length; k++) {
      if ((percentChildren[k] > 0) && (i > 0))
      {
        paramArrayOfInt[k] = (percentChildren[k] * i / 100);
        j -= paramArrayOfInt[k];
      }
      else if ((percentChildren[k] > 0) && (i <= 0))
      {
        paramArrayOfInt[k] = (paramInt / paramArrayOfInt.length);
        j -= paramArrayOfInt[k];
      }
    }
    if ((j > 0) && (relativeTotals > 0))
    {
      for (k = 0; k < paramArrayOfInt.length; k++) {
        if (relativeChildren[k] > 0) {
          paramArrayOfInt[k] = (j * relativeChildren[k] / relativeTotals);
        }
      }
    }
    else if (j > 0)
    {
      float f = paramInt - j;
      float[] arrayOfFloat = new float[paramArrayOfInt.length];
      j = paramInt;
      for (int m = 0; m < paramArrayOfInt.length; m++)
      {
        arrayOfFloat[m] = (paramArrayOfInt[m] / f * 100.0F);
        paramArrayOfInt[m] = ((int)(paramInt * arrayOfFloat[m] / 100.0F));
        j -= paramArrayOfInt[m];
      }
      m = 0;
      while (j != 0)
      {
        if (j < 0)
        {
          paramArrayOfInt[(m++)] -= 1;
          j++;
        }
        else
        {
          paramArrayOfInt[(m++)] += 1;
          j--;
        }
        if (m == paramArrayOfInt.length) {
          m = 0;
        }
      }
    }
  }
  
  private int parseDigits(String paramString)
  {
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      char c = paramString.charAt(j);
      if (Character.isDigit(c)) {
        i = i * 10 + Character.digit(c, 10);
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\FrameSetView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */