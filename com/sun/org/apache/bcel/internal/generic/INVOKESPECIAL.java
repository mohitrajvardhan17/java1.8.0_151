package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class INVOKESPECIAL
  extends InvokeInstruction
{
  INVOKESPECIAL() {}
  
  public INVOKESPECIAL(int paramInt)
  {
    super((short)183, paramInt);
  }
  
  public Class[] getExceptions()
  {
    Class[] arrayOfClass = new Class[4 + ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length];
    System.arraycopy(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION, 0, arrayOfClass, 0, ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length);
    arrayOfClass[(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length + 3)] = ExceptionConstants.UNSATISFIED_LINK_ERROR;
    arrayOfClass[(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length + 2)] = ExceptionConstants.ABSTRACT_METHOD_ERROR;
    arrayOfClass[(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length + 1)] = ExceptionConstants.INCOMPATIBLE_CLASS_CHANGE_ERROR;
    arrayOfClass[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length] = ExceptionConstants.NULL_POINTER_EXCEPTION;
    return arrayOfClass;
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitExceptionThrower(this);
    paramVisitor.visitTypedInstruction(this);
    paramVisitor.visitStackConsumer(this);
    paramVisitor.visitStackProducer(this);
    paramVisitor.visitLoadClass(this);
    paramVisitor.visitCPInstruction(this);
    paramVisitor.visitFieldOrMethod(this);
    paramVisitor.visitInvokeInstruction(this);
    paramVisitor.visitINVOKESPECIAL(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\INVOKESPECIAL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */