package lk.janith.gymadmin.controller;

import jakarta.servlet.http.HttpServletResponse;
import lk.janith.gymadmin.service.CsvExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reports/export")
public class CsvExportController {

    private final CsvExportService csvExportService;

    @GetMapping("/members")
    public void exportAllMembers(HttpServletResponse response) throws IOException {
        csvExportService.exportAllMembers(response);
    }

    @GetMapping("/members/active")
    public void exportActiveMembers(HttpServletResponse response) throws IOException {
        csvExportService.exportActiveMembers(response);
    }

    @GetMapping("/members/expired")
    public void exportExpiredMembers(HttpServletResponse response) throws IOException {
        csvExportService.exportExpiredMembers(response);
    }

    @GetMapping("/payments")
    public void exportPayments(HttpServletResponse response) throws IOException {
        csvExportService.exportPayments(response);
    }

    @GetMapping("/access-logs")
    public void exportAccessLogs(HttpServletResponse response) throws IOException {
        csvExportService.exportAccessLogs(response);
    }
}