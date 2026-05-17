package com.tirana.events.service;

import com.tirana.events.dto.WeatherForecastDTO;
import com.tirana.events.model.WeatherData;
import com.tirana.events.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherDataRepository weatherRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // Open-Meteo API (free, no key required)
    private static final String WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=41.3275&longitude=19.8187&hourly=temperature_2m,precipitation_probability,windspeed_10m,relativehumidity_2m,weathercode&timezone=Europe/Tirane";

    public List<WeatherForecastDTO> get5DayForecast() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveDaysLater = now.plusDays(5);
        
        List<WeatherData> forecasts = weatherRepository.findByDateRange(now, fiveDaysLater);
        
        if (forecasts.isEmpty()) {
            fetchAndSaveWeatherData();
            forecasts = weatherRepository.findByDateRange(now, fiveDaysLater);
        }
        
        return forecasts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public WeatherForecastDTO getWeatherForDate(LocalDateTime date) {
        WeatherData weather = weatherRepository.findFirstByForecastDateOrderByFetchedAtDesc(date)
            .orElseGet(() -> {
                fetchAndSaveWeatherData();
                return weatherRepository.findFirstByForecastDateOrderByFetchedAtDesc(date).orElse(null);
            });
        
        return weather != null ? convertToDTO(weather) : null;
    }

    private void fetchAndSaveWeatherData() {
        try {
            // In production, parse actual API response
            // For now, create sample data
            LocalDateTime now = LocalDateTime.now();
            
            for (int i = 0; i < 5; i++) {
                WeatherData weather = new WeatherData();
                weather.setForecastDate(now.plusDays(i));
                weather.setTemperature(20.0 + (Math.random() * 10));
                weather.setCondition(getRandomCondition());
                weather.setPrecipitationChance((int)(Math.random() * 100));
                weather.setWindSpeed(5.0 + (Math.random() * 15));
                weather.setHumidity(50 + (int)(Math.random() * 30));
                weather.setWeatherIcon(getWeatherIcon(weather.getCondition()));
                weather.setFetchedAt(LocalDateTime.now());
                
                weatherRepository.save(weather);
            }
        } catch (Exception e) {
            // Log error
        }
    }

    private String getRandomCondition() {
        String[] conditions = {"CLEAR", "CLOUDY", "RAIN", "STORM"};
        return conditions[(int)(Math.random() * conditions.length)];
    }

    private String getWeatherIcon(String condition) {
        switch (condition) {
            case "CLEAR": return "☀️";
            case "CLOUDY": return "☁️";
            case "RAIN": return "🌧️";
            case "STORM": return "⛈️";
            case "SNOW": return "❄️";
            default: return "🌤️";
        }
    }

    private WeatherForecastDTO convertToDTO(WeatherData weather) {
        WeatherForecastDTO dto = new WeatherForecastDTO();
        dto.setForecastDate(weather.getForecastDate());
        dto.setTemperature(weather.getTemperature());
        dto.setCondition(weather.getCondition());
        dto.setPrecipitationChance(weather.getPrecipitationChance());
        dto.setWindSpeed(weather.getWindSpeed());
        dto.setHumidity(weather.getHumidity());
        dto.setWeatherIcon(weather.getWeatherIcon());
        
        // Generate warning
        if (weather.getPrecipitationChance() > 70) {
            dto.setWarning("High chance of rain - consider indoor alternatives");
        } else if (weather.getCondition().equals("STORM")) {
            dto.setWarning("Storm warning - outdoor events may be affected");
        }
        
        return dto;
    }
}
