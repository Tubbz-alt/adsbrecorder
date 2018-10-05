package adsbrecorder.controller;

import static adsbrecorder.entity.Flight.isFlightNumber;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.entity.TrackingRecord;
import adsbrecorder.service.TrackingRecordService;

@RestController
public class TrackingRecordController {

    private TrackingRecordService trackingRecordService;

    @Autowired
    public TrackingRecordController(TrackingRecordService trackingRecordService) {
        this.trackingRecordService = requireNonNull(trackingRecordService);
    }

    @GetMapping("/api/{f}/{dt}")
    public List<TrackingRecord> historyOn(@PathVariable(value="f") String flightNumber,
            @PathVariable(value = "dt") @DateTimeFormat(pattern="yyyy-MM-dd") Date on) {
        if (isFlightNumber(flightNumber)) {
            return trackingRecordService.getTrackOn(flightNumber, on);
        }
        return Collections.emptyList();
    }

    @GetMapping("/api/{f}/latest")
    public TrackingRecord latestTrackingRecord(@PathVariable(value="f") String flightNumber) {
        if (isFlightNumber(flightNumber)) {
            TrackingRecord t = trackingRecordService.latestRecord(flightNumber);
            if (t != null)
                return t;
        }
        return TrackingRecord.emptyRecord();
    }

    @GetMapping("/api/{f}/history")
    public List<TrackingRecord> allHistory(@PathVariable(value="f") String flightNumber,
            @RequestParam(value="p", defaultValue = "0") String page,
            @RequestParam(value="n", defaultValue = "5") String amount) {
        if (isFlightNumber(flightNumber)) {
            int p, n;
            try {
                p = Integer.parseInt(page.replace('-', '?'));
            } catch (NumberFormatException e) {
                p = 0;
            }
            try {
                n = Integer.parseInt(amount.replace('-', '?'));
            } catch (NumberFormatException e) {
                n = 5;
            }
            List<TrackingRecord> records = trackingRecordService.trackingHistory(flightNumber, p, n);
            if (!records.isEmpty())
                return records;
        }
        return Collections.emptyList();
    }
}
