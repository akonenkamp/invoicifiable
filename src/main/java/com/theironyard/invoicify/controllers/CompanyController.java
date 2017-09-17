package com.theironyard.invoicify.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.theironyard.invoicify.models.Company;
import com.theironyard.invoicify.repositories.CompanyRepository;



@Controller
@RequestMapping("/admin/companies")
public class CompanyController {
	

@Autowired
private CompanyRepository companyRepository;


	
	@GetMapping("")
	public ModelAndView showCompanies() {
		ModelAndView mv = new ModelAndView ("admin/companies");
		mv.addObject("companies", companyRepository.findAll());
		return mv;
	}

	
	@PostMapping("")  
	public ModelAndView showAddedCompanies(Company company) {
		ModelAndView mv = new ModelAndView ("redirect:/admin/companies");
		companyRepository.save(company);
		return mv;
		
	}
}
	
