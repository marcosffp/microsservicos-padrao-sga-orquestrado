package br.com.microservices.orchestrated.orderservice.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.dto.EventFilters;

import br.com.microservices.orchestrated.orderservice.core.service.EventService;


@RestController
@RequestMapping("/api/event")
public class EventController {
  
  @Autowired
  private EventService eventService;

  @GetMapping
  public Event findByFilters(EventFilters filters) {
    return eventService.findByFilters(filters);
  }


  @GetMapping("all")
  public List<Event> findAll() {
    return eventService.findAll();
  }
}
