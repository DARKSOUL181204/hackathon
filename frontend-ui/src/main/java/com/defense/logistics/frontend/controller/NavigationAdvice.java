package com.defense.logistics.frontend.controller;

import com.defense.logistics.frontend.service.BackendClientService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class NavigationAdvice {
    private final BackendClientService backendService;

    @ModelAttribute
    void addNavigationState(Model model, HttpSession session) {
        if ("ADMIN".equals(session.getAttribute("role"))) model.addAttribute("pendingAccessRequests", backendService.getPendingAccessRequestCount());
    }
}
