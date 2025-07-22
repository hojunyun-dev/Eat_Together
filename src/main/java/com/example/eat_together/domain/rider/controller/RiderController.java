package com.example.eat_together.domain.rider.controller;

import com.example.eat_together.domain.rider.entity.Rider;
import com.example.eat_together.domain.rider.service.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/riders")
public class RiderController {

    private final RiderService riderService;

    //라이더 등록API
    @PostMapping
    public Rider createRider(@RequestParam String name, @RequestParam String phone) {
        return riderService.createRider(name, phone);
    }

    //전체 라이더 목록 조회API
    @GetMapping
    public List<Rider> getAllRiders() {
        return riderService.getAllRiders();
    }

    //라이더 조회 API
    @GetMapping("/{id}")
    public Rider getRider(@PathVariable Long id) {
        return riderService.getRiderById(id);
    }
}
