package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import java.util.Vector;

public abstract interface XSCMValidator
{
  public static final short FIRST_ERROR = -1;
  public static final short SUBSEQUENT_ERROR = -2;
  
  public abstract int[] startContentModel();
  
  public abstract Object oneTransition(QName paramQName, int[] paramArrayOfInt, SubstitutionGroupHandler paramSubstitutionGroupHandler);
  
  public abstract boolean endContentModel(int[] paramArrayOfInt);
  
  public abstract boolean checkUniqueParticleAttribution(SubstitutionGroupHandler paramSubstitutionGroupHandler)
    throws XMLSchemaException;
  
  public abstract Vector whatCanGoHere(int[] paramArrayOfInt);
  
  public abstract ArrayList checkMinMaxBounds();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSCMValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */