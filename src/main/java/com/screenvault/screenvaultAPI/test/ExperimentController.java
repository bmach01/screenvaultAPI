package com.screenvault.screenvaultAPI.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RequestMapping("/test")
@RestController
public class ExperimentController {

    public ExperimentController() {}

    @GetMapping("/noAuth/principal")
    public String principal(
            Principal principal
    ) {
        return principal == null ? "anonymous" : principal.getName();
    }

}
