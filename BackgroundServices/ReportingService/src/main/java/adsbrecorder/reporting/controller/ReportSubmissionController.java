package adsbrecorder.reporting.controller;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.common.aop.CheckOwnership;
import adsbrecorder.common.aop.RequireLogin;
import adsbrecorder.common.aop.RequireOwnership;
import adsbrecorder.reporting.ReportingServiceMappings;
import adsbrecorder.reporting.entity.ReportJob;
import adsbrecorder.reporting.service.ReportService;
import adsbrecorder.reporting.service.impl.ReportJobOwnershipValidator;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

@RestController
public class ReportSubmissionController implements ReportingServiceMappings {

    private UserService userService;
    private ReportService reportService;

    @Autowired
    public ReportSubmissionController(UserService userService,
            ReportService reportService) {
        this.userService = requireNonNull(userService);
        this.reportService = requireNonNull(reportService);
    }

    @RequireLogin
    @PostMapping(SIMPLE_DAILY_SUMMARY_REPORT)
    public ResponseEntity<Map<String, Object>> submitSimpleDailySummaryReport(
            @RequestParam(name = "name") String reportName,
            @RequestParam(name = "day") @DateTimeFormat(pattern="yyyy-MM-dd") Date day) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loginHash(String.valueOf(auth.getPrincipal()),
                String.valueOf(auth.getCredentials()));
        Map<String, Object> params = Map.of("day", day);
        ReportJob job = reportService.runSimpleDailySummaryReport(reportName,
                ReportService.SIMPLE_DAILY_SUMMARY_REPORT_TYPE, params, user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("SIMPLE_DAILY_SUMMARY_REPORT", job));
    }

    @RequireOwnership
    @GetMapping(GET_REPORT_PROGRESS)
    public ResponseEntity<Map<String, Object>> getReportProgress(@PathVariable(name = "id")
            @CheckOwnership(validator = ReportJobOwnershipValidator.class) BigInteger id) {
        ReportJob job = reportService.getById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("SIMPLE_DAILY_SUMMARY_REPORT", job));
    }
}
