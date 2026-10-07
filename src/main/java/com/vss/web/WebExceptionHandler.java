package com.vss.web;

import com.vss.service.DuplicateResourceException;
import com.vss.service.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Friendly error page for HTML flows (API errors are handled by ApiExceptionHandler). */
@ControllerAdvice(basePackages = "com.vss.web")
public class WebExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, DuplicateResourceException.class,
            IllegalStateException.class, IllegalArgumentException.class})
    public String handleDomainErrors(RuntimeException e, HttpServletRequest request, Model model) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }
}
