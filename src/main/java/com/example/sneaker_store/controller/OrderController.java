package com.example.sneaker_store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j(topic = "ORDER-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("/order/v1")
public class OrderController {
}
