package com.spectred.controller;

import com.spectred.beans.Autowired;
import com.spectred.service.SalaryService;
import com.spectred.web.mvc.Controller;
import com.spectred.web.mvc.RequestMapping;
import com.spectred.web.mvc.RequestParam;

@Controller
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @RequestMapping("/getSalary.json")
    public Integer get(@RequestParam("name") String name, @RequestParam("exp") String exp) {
        return salaryService.getSalary(Integer.valueOf(exp));
    }
}
