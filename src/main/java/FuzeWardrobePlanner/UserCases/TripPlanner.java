package FuzeWardrobePlanner.UserCases;

import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
import FuzeWardrobePlanner.Entity.Clothing.Outfit;
import FuzeWardrobePlanner.Entity.Clothing.WardrobeRepository;
import FuzeWardrobePlanner.Entity.Weather.WeatherDay;
import FuzeWardrobePlanner.Entity.Weather.WeatherTrip;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Use case for planning trip outfits independently of any UI.
 */
public class TripPlanner {

    private final WardrobeRepository wardrobeRepository;
    private final OutfitCreator outfitCreator;

    public TripPlanner(WardrobeRepository wardrobeRepository, OutfitCreator outfitCreator) {
        this.wardrobeRepository = wardrobeRepository;
        this.outfitCreator = outfitCreator != null ? outfitCreator : new OutfitCreator();
    }

    /**
     * Plan outfits for a trip starting at the given date.
     *
     * @param location      the location string for the trip
     * @param startDate     trip start date
     * @param outfitsNeeded number of outfits requested (capped at 7)
     * @return list of WeatherDay entries for the planned trip with outfits assigned when possible
     */
    public List<WeatherDay> planTrip(String location, LocalDate startDate, int outfitsNeeded) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("location cannot be empty");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate is required");
        }

        int days = Math.min(7, Math.max(0, outfitsNeeded));
        if (days == 0) {
            return List.of();
        }

        WeatherTrip trip = new WeatherTrip(location, startDate.toString(), days);
        Map<String, List<ClothingArticle>> wardrobeMap = buildWardrobeMap(
                wardrobeRepository != null ? wardrobeRepository.getAll() : List.of()
        );
        Set<String> previousNames = new HashSet<>();
        List<WeatherDay> plannedDays = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            WeatherDay day = trip.getWeatherDay(i);
            if (day == null) {
                LocalDate date = startDate.plusDays(i);
                day = new WeatherDay(0, 0, 0, trip.getLocation(), date.toString());
            } else {
                Outfit outfit = wardrobeMap.isEmpty()
                        ? null
                        : generateOutfitWithNoRepeat(day, wardrobeMap, previousNames);
                day.setOutfit(outfit);
                previousNames = extractNames(outfit);
            }
            plannedDays.add(day);
        }

        return plannedDays;
    }

    private Map<String, List<ClothingArticle>> buildWardrobeMap(List<ClothingArticle> items) {
        Map<String, List<ClothingArticle>> map = new HashMap<>();
        map.put("top", new ArrayList<>());
        map.put("bottom", new ArrayList<>());
        map.put("outer", new ArrayList<>());
        map.put("accessory", new ArrayList<>());

        for (ClothingArticle item : items) {
            if (item == null || item.getCategory() == null) continue;
            String cat = item.getCategory().toLowerCase();
            if (cat.contains("top")) {
                map.get("top").add(item);
            } else if (cat.contains("bottom") || cat.contains("pant") || cat.contains("bottoms")) {
                map.get("bottom").add(item);
            } else if (cat.contains("outer")) {
                map.get("outer").add(item);
            } else if (cat.contains("access")) {
                map.get("accessory").add(item);
            }
        }
        return map;
    }

    private Outfit generateOutfitWithNoRepeat(WeatherDay day,
                                              Map<String, List<ClothingArticle>> wardrobeMap,
                                              Set<String> previousNames) {
        Outfit first = outfitCreator.createOutfitForDay(day, shuffledCopy(wardrobeMap), false);
        if (first == null) return null;
        if (!isSameTopOrBottom(first, previousNames)) {
            return first;
        }

        for (int attempt = 0; attempt < 4; attempt++) {
            Outfit candidate = outfitCreator.createOutfitForDay(day, shuffledCopy(wardrobeMap), false);
            if (candidate != null && !isSameTopOrBottom(candidate, previousNames)) {
                return candidate;
            }
        }

        Map<String, List<ClothingArticle>> filtered = new HashMap<>();
        for (Map.Entry<String, List<ClothingArticle>> entry : wardrobeMap.entrySet()) {
            List<ClothingArticle> list = new ArrayList<>();
            for (ClothingArticle item : entry.getValue()) {
                if (item != null && (previousNames == null || !previousNames.contains(item.getName()))) {
                    list.add(item);
                }
            }
            filtered.put(entry.getKey(), list);
        }
        Outfit lastTry = outfitCreator.createOutfitForDay(day, filtered, false);
        return lastTry != null ? lastTry : first;
    }

    private Map<String, List<ClothingArticle>> shuffledCopy(Map<String, List<ClothingArticle>> original) {
        Map<String, List<ClothingArticle>> copy = new HashMap<>();
        for (Map.Entry<String, List<ClothingArticle>> entry : original.entrySet()) {
            List<ClothingArticle> list = new ArrayList<>(entry.getValue());
            Collections.shuffle(list);
            copy.put(entry.getKey(), list);
        }
        return copy;
    }

    private boolean isSameTopOrBottom(Outfit outfit, Set<String> previousNames) {
        if (outfit == null || previousNames == null) return false;
        Map<String, ClothingArticle> items = outfit.getItems();
        if (items == null) return false;
        ClothingArticle top = items.get("top");
        ClothingArticle bottom = items.get("bottom");
        boolean sameTop = top != null && top.getName() != null && previousNames.contains(top.getName());
        boolean sameBottom = bottom != null && bottom.getName() != null && previousNames.contains(bottom.getName());
        return sameTop && sameBottom;
    }

    private Set<String> extractNames(Outfit outfit) {
        Set<String> names = new HashSet<>();
        if (outfit == null || outfit.getItems() == null) {
            return names;
        }
        for (ClothingArticle article : outfit.getItems().values()) {
            if (article != null && article.getName() != null) {
                names.add(article.getName());
            }
        }
        return names;
    }
}
