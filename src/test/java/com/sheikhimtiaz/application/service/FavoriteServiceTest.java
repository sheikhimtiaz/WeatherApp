package com.sheikhimtiaz.application.service;

import com.sheikhimtiaz.application.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class FavoriteServiceTest {

    @Autowired
    private FavoriteLocationService favoriteService;

    @BeforeEach
    void setUp() {
        favoriteService = new FavoriteLocationService();
    }

    @Test
    void getFavorites_WhenUserHasNoFavorites_ReturnsEmptyList() {
        assertTrue(favoriteService.getFavorites("user1").isEmpty());
    }

    @Test
    void addAndGetFavorites_WhenUserHasFavorites_ReturnsFavorites() {
        String userId = "user2";
        Location location = createLocation(1, "New York", "USA");
        favoriteService.addFavorite(userId, location);

        List<Location> favorites = favoriteService.getFavorites(userId);
        assertFalse(favorites.isEmpty());
        assertTrue(favorites.contains(location));
    }

    @Test
    void addFavorite_AddsToFavoritesCorrectly() {
        String userId = "user3";
        Location location1 = createLocation(2, "Paris", "France");
        Location location2 = createLocation(3, "Berlin", "Germany");

        favoriteService.addFavorite(userId, location1);
        favoriteService.addFavorite(userId, location2);

        List<Location> favorites = favoriteService.getFavorites(userId);
        assertEquals(2, favorites.size());
        assertTrue(favorites.contains(location1));
        assertTrue(favorites.contains(location2));
    }

    @Test
    void removeFavorite_RemovesFavoriteCorrectly() {
        String userId = "user4";
        Location location = createLocation(4, "Tokyo", "Japan");
        favoriteService.addFavorite(userId, location);

        favoriteService.removeFavorite(userId, location);
        List<Location> favorites = favoriteService.getFavorites(userId);
        assertFalse(favorites.contains(location));
    }

    @Test
    void removeFavorite_WhenFavoriteDoesNotExist_ListUnchanged() {
        String userId = "user5";
        Location location = createLocation(5, "Seoul", "South Korea");
        favoriteService.addFavorite(userId, location);

        Location nonExistentLocation = createLocation(6, "London", "UK");
        favoriteService.removeFavorite(userId, nonExistentLocation);

        List<Location> favorites = favoriteService.getFavorites(userId);
        assertEquals(1, favorites.size());
        assertTrue(favorites.contains(location));
    }

    private Location createLocation(long id, String name, String country) {
        Location location = new Location();
        location.setId(id);
        location.setName(name);
        // Assuming setting country sets both country name and country code for simplicity
        location.setCountry(country);
        // Additional properties can be set here if needed
        return location;
    }
}