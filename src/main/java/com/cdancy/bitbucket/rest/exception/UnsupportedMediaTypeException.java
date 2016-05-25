package com.cdancy.bitbucket.rest.exception;

/**
 * The request entity has a Content-Type that the server does not support.
 * Almost all of the Bitbucket REST API accepts application/json format, but
 * check the individual resource documentation for more details. Additionally,
 * double-check that you are setting the Content-Type header correctly on your
 * request (e.g. using -H "Content-Type: application/json" in cURL).
 */
public class UnsupportedMediaTypeException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   public UnsupportedMediaTypeException() {
      super();
   }

   public UnsupportedMediaTypeException(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public UnsupportedMediaTypeException(String arg0) {
      super(arg0);
   }

   public UnsupportedMediaTypeException(Throwable arg0) {
      super(arg0);
   }

}