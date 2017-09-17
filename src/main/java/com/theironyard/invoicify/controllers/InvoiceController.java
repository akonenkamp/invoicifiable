package com.theironyard.invoicify.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.theironyard.invoicify.models.BillingRecord;
import com.theironyard.invoicify.models.Invoice;
import com.theironyard.invoicify.models.InvoiceLineItem;
import com.theironyard.invoicify.models.User;
import com.theironyard.invoicify.repositories.BillingRecordRepository;
import com.theironyard.invoicify.repositories.CompanyRepository;
import com.theironyard.invoicify.repositories.InvoiceRepository;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private BillingRecordRepository recordRepository;

	@GetMapping("new")
	public ModelAndView stepOne()	{
		ModelAndView mv = new ModelAndView ("invoices/step-1");
		mv.addObject("companies", companyRepository.findAll());
		
		return mv;
	}

	
	
	@GetMapping("")
	public ModelAndView list(Authentication auth) {
		User user = (User) auth.getPrincipal();
		ModelAndView mv = new ModelAndView("invoices/list");
		mv.addObject("user", user);
		mv.addObject("invoices", invoiceRepository.findAll());
		return mv;
	}

	
	@PostMapping ("new")
	public ModelAndView stepTwo(long clientId) {
		ModelAndView mv = new ModelAndView("invoices/step-2");
		mv.addObject("records", recordRepository.findByClientIdAndLineItemIsNull(clientId));
//		mv.addObject("companies", companyRepository.findAll());
		mv.addObject("clientId", clientId);
		mv.addObject("errorMessage", "");
		return mv;
	}
	
	@PostMapping ("create") 
	public ModelAndView createInvoice(Invoice invoice, long clientId, long[] recordIds, Authentication auth) {
	ModelAndView mv = new ModelAndView();
	try {
			
		
		User creator = (User) auth.getPrincipal();
		List<BillingRecord> records = recordRepository.findByIdIn(recordIds);
		Long nowIsh = Calendar.getInstance().getInstance().getTimeInMillis();
		Date now = new Date(nowIsh);
		
		List<InvoiceLineItem> items = new ArrayList<InvoiceLineItem>();
		for (BillingRecord record : records) {
			InvoiceLineItem lineItem = new InvoiceLineItem();
			lineItem.setCreatedBy(creator);
			lineItem.setCreatedOn(now);
			lineItem.setInvoice(invoice);
			lineItem.setBillingRecord(record);
			items.add(lineItem);
		}
		
		invoice.setCompany(companyRepository.findOne(clientId));
		invoice.setCreatedBy(creator);
		invoice.setCreatedOn(now);
		invoice.setLineItems(items);
		invoiceRepository.save(invoice);
		mv.setViewName("redirect:/invoices");
		
	}
	 
	catch (InvalidDataAccessApiUsageException ida){
		System.err.println(ida.getClass().getName());
		mv.addObject("errorMessage", "Please select at least one billing record.");
		mv.addObject("clientId", clientId);
		mv.addObject("records", recordRepository.findByClientIdAndLineItemIsNull(clientId));
		mv.setViewName("/invoices/step-2");		
	}
	
	return mv;
	
}
}
















