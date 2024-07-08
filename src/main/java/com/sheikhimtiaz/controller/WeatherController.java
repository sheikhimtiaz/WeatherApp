package com.sheikhimtiaz.controller;

import com.sheikhimtiaz.data.model.WeatherData;
import com.sheikhimtiaz.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather")
    public WeatherData getWeather(@RequestParam double latitude, @RequestParam double longitude) throws IOException {
        return weatherService.getWeatherData(latitude, longitude);
    }
}
