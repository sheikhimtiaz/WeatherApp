package com.sheikhimtiaz.views.search;

import com.sheikhimtiaz.data.model.Location;
import com.sheikhimtiaz.data.model.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestTemplate;
import com.sheikhimtiaz.views.MainLayout;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sheikhimtiaz.constant.Constants.APP_BASE_URL;

@Route(value = "search", layout = MainLayout.class)
public class LocationSearchView extends VerticalLayout {

    private TextField cityNameField;
    private TextField filterTextField;
    private Grid<Location> grid;
    private Button searchButton;
    private int currentPage = 0;
    private final int pageSize = 10;
    private VerticalLayout weatherLayout;

    public LocationSearchView() {
        cityNameField = new TextField("City Name");
        filterTextField = new TextField("Filter by Location Name");
        searchButton = new Button("Search", e -> searchLocations());

        grid = new Grid<>(Location.class);
        grid.setColumns("name", "latitude", "longitude");
        grid.asSingleSelect().addValueChangeListener(event -> {
            Location selectedLocation = event.getValue();
            if (selectedLocation != null) {
                showWeather(selectedLocation);
            }
        });

        Button nextPageButton = new Button("Next Page", e -> loadNextPage());
        Button previousPageButton = new Button("Previous Page", e -> loadPreviousPage());

        weatherLayout = new VerticalLayout();
        HorizontalLayout searchLayout = new HorizontalLayout(cityNameField, searchButton);
        searchLayout.setAlignItems(Alignment.END);
        add(searchLayout, grid, previousPageButton, nextPageButton, weatherLayout);
    }

    private void showWeather(Location location) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = String.format(APP_BASE_URL + "/weather?latitude=%f&longitude=%f",
                location.getLatitude(), location.getLongitude());
        try {
            WeatherData weatherData = restTemplate.getForObject(apiUrl, WeatherData.class);
            weatherLayout.removeAll();
            weatherLayout.add(new H2("Weather Forecast for " + location.getName()));
            // Show daily forecast
            Grid<WeatherDaily> dailyGrid = new Grid<>(WeatherDaily.class);
            dailyGrid.setItems(weatherData.getDailyWeatherData());
            dailyGrid.addColumn(WeatherDaily::getDate).setHeader("Date");
            dailyGrid.addColumn(WeatherDaily::getTemperatureMax).setHeader("Max Temperature");
            dailyGrid.addColumn(WeatherDaily::getTemperatureMin).setHeader("Min Temperature");
            dailyGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(daily -> showHourlyWeather(weatherData, daily.getDate())));
            weatherLayout.add(dailyGrid);
            weatherLayout.add(showWeatherCharts(location, weatherData));
        } catch (Exception e) {
            Notification.show("Failed to fetch weather data: " + e.getMessage());
        }
    }

    private Component showWeatherCharts(Location location, WeatherData weatherData) {
        // Header
        Select year = new Select();
        year.setItems(location.getName());
        year.setValue(location.getName());
        year.setWidth("100px");

        HorizontalLayout header = createHeader("Charts for weather data", "Next 7 days (location)");
        header.add(year);

        // Chart
        Chart chart = new Chart(ChartType.AREA);
        Configuration conf = chart.getConfiguration();

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Day 1","Day 2","Day 3","Day 4","Day 5","Day 6","Day 7");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Values");

        PlotOptionsArea plotOptions = new PlotOptionsArea();
        plotOptions.setPointPlacement(PointPlacement.ON);
        conf.addPlotOptions(plotOptions);

        conf.addSeries(new ListSeries(location.getName(), weatherData.getDailyTemperatureMax().toArray(new Double[0])));

        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName("p-l");
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        return viewEvents;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames("text-xl", "m-0");

        Span span = new Span(subtitle);
        span.addClassNames("text-secondary", "text-xs");

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }

    private void showHourlyWeather(WeatherData weatherData, String date) {
        List<WeatherHourly> hourlyWeatherForDate = weatherData.getHourlyWeatherDataForDate(date);

        Grid<WeatherHourly> hourlyGrid = new Grid<>(WeatherHourly.class);
        hourlyGrid.setItems(hourlyWeatherForDate);
        hourlyGrid.addColumn(WeatherHourly::getTime).setHeader("Time");
        hourlyGrid.addColumn(WeatherHourly::getTemperature).setHeader("Temperature");
        hourlyGrid.addColumn(WeatherHourly::getWindspeed).setHeader("Wind Speed");
        hourlyGrid.addColumn(WeatherHourly::getPrecipitation).setHeader("Precipitation");
        weatherLayout.add(hourlyGrid);
    }

    private void searchLocations() {
        currentPage = 0;
        fetchAndDisplayLocations();
    }

    private void loadNextPage() {
        currentPage++;
        fetchAndDisplayLocations();
    }

    private void loadPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            fetchAndDisplayLocations();
        }
    }

    private void fetchAndDisplayLocations() {
        RestTemplate restTemplate = new RestTemplate();
        String cityName = cityNameField.getValue();
        String filterText = filterTextField.getValue().toLowerCase();
        String apiUrl = String.format(APP_BASE_URL+"/locations?cityName=%s&page=%d&size=%d",
                cityName, currentPage, pageSize);
        try {
            Location[] locations = restTemplate.getForObject(apiUrl, Location[].class);
//            List<Location> filteredLocations = Arrays.stream(locations)
//                    .filter(location -> location.getName().toLowerCase().contains(filterText))
//                    .toList();
            List<Location> filteredLocations = Arrays.stream(Objects.requireNonNull(locations))
//                    .filter(location -> location.getName().toLowerCase().contains(filterText))
                    .collect(Collectors.toList());
            grid.setItems(filteredLocations);
        } catch (Exception e) {
            Notification.show("Failed to fetch locations: " + e.getMessage());
        }
    }
}
