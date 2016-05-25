package com.cdancy.bitbucket.rest.exception;

/**
 * Thrown when a method was used that is not supported by this endpoint
 */
public class MethodNotAllowedException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   public MethodNotAllowedException() {
      super();
   }

   public MethodNotAllowedException(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public MethodNotAllowedException(String arg0) {
      super(arg0);
   }

   public MethodNotAllowedException(Throwable arg0) {
      super(arg0);
   }

}