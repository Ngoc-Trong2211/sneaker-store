package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.response.DashboardResponse;
import com.example.sneaker_store.dto.response.FavouriteResponse;
import com.example.sneaker_store.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard/v1")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/statistic")
    public ResponseEntity<DashboardResponse> getStatistic() {
        return ResponseEntity.ok(dashboardService.getDashboardStatistic());
    }

    @GetMapping("/export-dashboard")
    public ResponseEntity<byte[]> exportDashboard() throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=dashboard.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(dashboardService.exportDashboardExcel());
    }

    @GetMapping("/top-pick")
    public ResponseEntity<List<FavouriteResponse>> getTopPick(@RequestParam String gender) {
        return ResponseEntity.ok(dashboardService.topPick(gender));
    }
}
