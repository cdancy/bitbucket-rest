package com.cdancy.bitbucket.rest.exception;

/**
 * Thrown when an action has breached the licensed user limit of the server, or
 * degrading the authenticated user's permission level.
 */
public class ForbiddenException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   public ForbiddenException() {
      super();
   }

   public ForbiddenException(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public ForbiddenException(String arg0) {
      super(arg0);
   }

   public ForbiddenException(Throwable arg0) {
      super(arg0);
   }

}