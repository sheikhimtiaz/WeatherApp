package com.sheikhimtiaz.controller;

import com.sheikhimtiaz.data.model.Location;
import com.sheikhimtiaz.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;

@RestController
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping("/locations")
    public List<Location> getLocations(@RequestParam String cityName, @RequestParam int page, @RequestParam int size) throws IOException {
        List<Location> allLocations = locationService.getLocationsByCityName(cityName, size);
        int start = page * size;
        int end = Math.min((page + 1) * size, allLocations.size());
        return allLocations.subList(start, end);
    }
}
