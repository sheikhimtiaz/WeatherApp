package com.sheikhimtiaz.application.service;


import com.sheikhimtiaz.application.model.Location;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {

    private Map<String, List<Location>> userFavorites = new HashMap<>();

    public List<Location> getFavorites(String userId) {
        return userFavorites.getOrDefault(userId, new ArrayList<>());
    }

    public void addFavorite(String userId, Location location) {
        userFavorites.computeIfAbsent(userId, k -> new ArrayList<>()).add(location);
    }

    public void removeFavorite(String userId, Location location) {
        List<Location> favorites = userFavorites.get(userId);
        if (favorites != null) {
            favorites.remove(location);
        }
    }
}
