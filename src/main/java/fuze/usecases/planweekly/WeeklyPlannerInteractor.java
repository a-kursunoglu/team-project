package fuze.usecases.planweekly;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.entity.weather.WeatherDay;
import fuze.entity.weather.WeatherWeek;
import fuze.usecases.generateoutfit.OutfitCreator;
import fuze.usecases.managewardrobe.WardrobeRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Use case interactor for planning weekly outfits.
 * Generates outfits for each day in a WeatherWeek while avoiding back-to-back duplicates.
 */
public class WeeklyPlannerInteractor {

    private final WardrobeRepository wardrobeRepository;
    private final OutfitCreator outfitCreator;

    public WeeklyPlannerInteractor(WardrobeRepository wardrobeRepository, OutfitCreator outfitCreator) {
        this.wardrobeRepository = wardrobeRepository;
        this.outfitCreator = outfitCreator;
    }

    /**
     * Generates outfits for each day in the provided WeatherWeek.
     * Applies a no-repeat rule for consecutive days.
     */
    public void generateOutfitsForWeek(WeatherWeek week) {
        if (week == null) {
            return;
        }

        Map<String, List<ClothingArticle>> wardrobeMap = buildWardrobeMap(wardrobeRepository.getAll());
        if (isWardrobeEmpty(wardrobeMap)) {
            return;
        }

        Set<String> prevNames = new HashSet<>();
        Outfit previousOutfit = null;

        for (int i = 0; i < 7; i++) {
            WeatherDay day = week.getWeatherDay(i);
            if (day == null) {
                continue;
            }
            Outfit outfit = generateOutfitWithNoRepeat(day, wardrobeMap, prevNames, previousOutfit);
            day.setOutfit(outfit);
            prevNames = extractNames(outfit);
            previousOutfit = outfit;
        }
    }

    private boolean isWardrobeEmpty(Map<String, List<ClothingArticle>> wardrobeMap) {
        for (List<ClothingArticle> items : wardrobeMap.values()) {
            if (items != null && !items.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Outfit generateOutfitWithNoRepeat(WeatherDay day,
                                              Map<String, List<ClothingArticle>> wardrobeMap,
                                              Set<String> previousNames,
                                              Outfit previousOutfit) {
        Outfit first = outfitCreator.createOutfitForDay(day, shuffledCopy(wardrobeMap), false);
        if (first == null) return null;
        if (!isSameTopOrBottom(first, previousNames) && !isSameOutfit(first, previousOutfit)) {
            return first;
        }

        for (int attempt = 0; attempt < 4; attempt++) {
            Outfit candidate = outfitCreator.createOutfitForDay(day, shuffledCopy(wardrobeMap), false);
            if (candidate != null && !isSameTopOrBottom(candidate, previousNames)
                    && !isSameOutfit(candidate, previousOutfit)) {
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
        if (lastTry != null && !isSameOutfit(lastTry, previousOutfit)) {
            return lastTry;
        }
        return first;
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
        if (outfit == null || outfit.getItems() == null) return names;
        for (ClothingArticle item : outfit.getItems().values()) {
            if (item != null && item.getName() != null) {
                names.add(item.getName());
            }
        }
        return names;
    }

    /**
     * Returns true if both outfits use the same named items for every slot.
     * A null outfit never matches another outfit.
     */
    private boolean isSameOutfit(Outfit a, Outfit b) {
        if (a == null || b == null) {
            return false;
        }
        Map<String, ClothingArticle> itemsA = a.getItems();
        Map<String, ClothingArticle> itemsB = b.getItems();
        if (itemsA == null || itemsB == null) {
            return false;
        }
        if (!itemsA.keySet().equals(itemsB.keySet())) {
            return false;
        }
        for (String key : itemsA.keySet()) {
            ClothingArticle articleA = itemsA.get(key);
            ClothingArticle articleB = itemsB.get(key);
            String nameA = articleA != null ? articleA.getName() : null;
            String nameB = articleB != null ? articleB.getName() : null;
            if (!Objects.equals(nameA, nameB)) {
                return false;
            }
        }
        return true;
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
}
