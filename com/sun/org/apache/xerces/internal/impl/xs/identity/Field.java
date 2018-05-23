package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath.Axis;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath.LocationPath;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath.Step;
import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.impl.xs.util.ShortListImpl;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class Field
{
  protected XPath fXPath;
  protected IdentityConstraint fIdentityConstraint;
  
  public Field(XPath paramXPath, IdentityConstraint paramIdentityConstraint)
  {
    fXPath = paramXPath;
    fIdentityConstraint = paramIdentityConstraint;
  }
  
  public XPath getXPath()
  {
    return fXPath;
  }
  
  public IdentityConstraint getIdentityConstraint()
  {
    return fIdentityConstraint;
  }
  
  public XPathMatcher createMatcher(FieldActivator paramFieldActivator, ValueStore paramValueStore)
  {
    return new Matcher(fXPath, paramFieldActivator, paramValueStore);
  }
  
  public String toString()
  {
    return fXPath.toString();
  }
  
  protected class Matcher
    extends XPathMatcher
  {
    protected FieldActivator fFieldActivator;
    protected ValueStore fStore;
    
    public Matcher(Field.XPath paramXPath, FieldActivator paramFieldActivator, ValueStore paramValueStore)
    {
      super();
      fFieldActivator = paramFieldActivator;
      fStore = paramValueStore;
    }
    
    protected void matched(Object paramObject, short paramShort, ShortList paramShortList, boolean paramBoolean)
    {
      super.matched(paramObject, paramShort, paramShortList, paramBoolean);
      if ((paramBoolean) && (fIdentityConstraint.getCategory() == 1))
      {
        String str = "KeyMatchesNillable";
        fStore.reportError(str, new Object[] { fIdentityConstraint.getElementName(), fIdentityConstraint.getIdentityConstraintName() });
      }
      fStore.addValue(Field.this, paramObject, convertToPrimitiveKind(paramShort), convertToPrimitiveKind(paramShortList));
      fFieldActivator.setMayMatch(Field.this, Boolean.FALSE);
    }
    
    private short convertToPrimitiveKind(short paramShort)
    {
      if (paramShort <= 20) {
        return paramShort;
      }
      if (paramShort <= 29) {
        return 2;
      }
      if (paramShort <= 42) {
        return 4;
      }
      return paramShort;
    }
    
    private ShortList convertToPrimitiveKind(ShortList paramShortList)
    {
      if (paramShortList != null)
      {
        int j = paramShortList.getLength();
        for (int i = 0; i < j; i++)
        {
          short s = paramShortList.item(i);
          if (s != convertToPrimitiveKind(s)) {
            break;
          }
        }
        if (i != j)
        {
          short[] arrayOfShort = new short[j];
          for (int k = 0; k < i; k++) {
            arrayOfShort[k] = paramShortList.item(k);
          }
          while (i < j)
          {
            arrayOfShort[i] = convertToPrimitiveKind(paramShortList.item(i));
            i++;
          }
          return new ShortListImpl(arrayOfShort, arrayOfShort.length);
        }
      }
      return paramShortList;
    }
    
    protected void handleContent(XSTypeDefinition paramXSTypeDefinition, boolean paramBoolean, Object paramObject, short paramShort, ShortList paramShortList)
    {
      if ((paramXSTypeDefinition == null) || ((paramXSTypeDefinition.getTypeCategory() == 15) && (((XSComplexTypeDefinition)paramXSTypeDefinition).getContentType() != 1))) {
        fStore.reportError("cvc-id.3", new Object[] { fIdentityConstraint.getName(), fIdentityConstraint.getElementName() });
      }
      fMatchedString = paramObject;
      matched(fMatchedString, paramShort, paramShortList, paramBoolean);
    }
  }
  
  public static class XPath
    extends XPath
  {
    public XPath(String paramString, SymbolTable paramSymbolTable, NamespaceContext paramNamespaceContext)
      throws XPathException
    {
      super(paramSymbolTable, paramNamespaceContext);
      for (int i = 0; i < fLocationPaths.length; i++) {
        for (int j = 0; j < fLocationPaths[i].steps.length; j++)
        {
          XPath.Axis localAxis = fLocationPaths[i].steps[j].axis;
          if ((type == 2) && (j < fLocationPaths[i].steps.length - 1)) {
            throw new XPathException("c-fields-xpaths");
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\identity\Field.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */