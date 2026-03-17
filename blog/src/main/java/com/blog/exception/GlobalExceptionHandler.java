package com.blog.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxSizeException(MaxUploadSizeExceededException exc) {
        ModelAndView mav = new ModelAndView("error/generic");
        mav.addObject("status", 400);
        mav.addObject("error", "Fichier trop volumineux");
        mav.addObject("message", "L'image ne doit pas dépasser 5 Mo.");
        return mav;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error/generic");
        mav.addObject("status", 404);
        mav.addObject("error", "Ressource introuvable");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFound(NoHandlerFoundException ex) {
        ModelAndView mav = new ModelAndView("error/generic");
        mav.addObject("status", 404);
        mav.addObject("error", "Page introuvable");
        mav.addObject("message", "La page demandée n'existe pas.");
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied() {
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericError(Exception ex) {
        ModelAndView mav = new ModelAndView("error/generic");
        mav.addObject("status", 500);
        mav.addObject("error", "Une erreur interne est survenue");
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
