package fuze.usecases.trippacking;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.entity.weather.WeatherDay;
import fuze.entity.weather.WeatherTrip;
import fuze.entity.weather.WeatherWeek;
import fuze.usecases.generateoutfit.OutfitCreator;
import fuze.usecases.managewardrobe.WardrobeRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TripPlannerInteractor {
    /** Wardrobe to select from.*/
    private final WardrobeRepository wardrobeRepository;
    /** Generic outfit creator.*/
    private final OutfitCreator outfitCreator;

    /**
     * Creates instance of TripPlannerInteractor.
     * @param repo the selected wardrobe repository
     * @param creator the generic outfit creator
     */
    public TripPlannerInteractor(final WardrobeRepository repo,
                                 final OutfitCreator creator) {
        this.wardrobeRepository = repo;
        this.outfitCreator = creator != null ? creator : new OutfitCreator();
    }

    /**
     *  Handles which dates are valid,
     *  i.e. whether start date is before end date
     *  and if dates are in proper format
     * @param start string of yyyy-mm-dd
     * @param end string of yyyy-mm-dd
     * @return the local dates available
     * @throws IllegalArgumentException if not of right format
     */
    public LocalDate[] validateDates(final String start, final String end)
            throws IllegalArgumentException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate s = LocalDate.parse(start, fmt);
            LocalDate e = LocalDate.parse(end, fmt);
            if (e.isBefore(s)) {
                throw new IllegalArgumentException(
                        "End date cannot be before start date");
            }
            return new LocalDate[]{s, e};

        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Dates must be in format yyyy-mm-dd.");
        }
    }
    /** Generate the full list of WeatherDay objects for the trip
     * based on WeatherTrip. This is really to only be used by the UI
     * @param location as a string (using the lookup functions)
     * @param start the local start date
     * @param days integer number of planned days
     * @return a list of WeatherDays
     */
    public List<WeatherDay> generateTripPlan(final String location,
                                             final LocalDate start,
                                             final int days) {
        // Creating the actual trip from the WeatherTrip, which helps handle API
        // calls and weather stuff.
        WeatherTrip trip = new WeatherTrip(location, start.toString(), days);
        Map<String, List<ClothingArticle>> wardrobeMap =
                buildWardrobeMap(wardrobeRepository
                        != null ? wardrobeRepository.getAll() : List.of());

        List<WeatherDay> result = new ArrayList<>();
        Set<String> prevNames = new HashSet<>();

        for (int i = 0; i < days; i++) {
            WeatherDay day = trip.getWeatherDay(i);
            if (day == null) {
                day = new WeatherWeek().getWeatherDay(i);
            } else {
                Outfit outfit = generateOutfitWithNoRepeat(day, wardrobeMap,
                        prevNames);
                day.setOutfit(outfit);
                prevNames = extractNames(outfit);
            }
            result.add(day);
        }
        return result;
    }


    private Map<String, List<ClothingArticle>> buildWardrobeMap(
            final List<ClothingArticle> items) {
        Map<String, List<ClothingArticle>> map = new HashMap<>();
        map.put("top", new ArrayList<>());
        map.put("bottom", new ArrayList<>());
        map.put("outer", new ArrayList<>());
        map.put("accessory", new ArrayList<>());

        for (ClothingArticle item : items) {
            if (item == null || item.getCategory() == null) {
                continue;
            }
            String cat = item.getCategory().toLowerCase();
            if (cat.contains("top")) {
                map.get("top").add(item);
            } else if (cat.contains("bottom") || cat.contains("pant")) {
                map.get("bottom").add(item);
            } else if (cat.contains("outer")) {
                map.get("outer").add(item);
            } else if (cat.contains("access")) {
                map.get("accessory").add(item);
            }
        }
        return map;
    }

    private Outfit generateOutfitWithNoRepeat(final WeatherDay day,
                                              final Map<String,
                                                      List<ClothingArticle>>
                                                      wardrobeMap,
                                              final Set<String> previousNames) {
        Outfit first = outfitCreator.createOutfitForDay(day,
                shuffledCopy(wardrobeMap), false);
        if (first == null) {
            return null;
        }
        if (!isSameTopOrBottom(first, previousNames)) {
            return first;
        }

        for (int i = 0; i < 4; i++) {
            Outfit c = outfitCreator.createOutfitForDay(day,
                    shuffledCopy(wardrobeMap), false);
            if (c != null && !isSameTopOrBottom(c, previousNames)) {
                return c;
            }
        }

        Map<String, List<ClothingArticle>> filtered = new HashMap<>();
        for (Map.Entry<String, List<ClothingArticle>>
                e : wardrobeMap.entrySet()) {
            List<ClothingArticle> list = new ArrayList<>();
            for (ClothingArticle a : e.getValue()) {
                if (!previousNames.contains(a.getName())) {
                    list.add(a);
                }
            }
            filtered.put(e.getKey(), list);
        }

        Outfit lastTry =
                outfitCreator.createOutfitForDay(day, filtered,
                        false);

        return lastTry != null ? lastTry : first;
    }

    private Map<String, List<ClothingArticle>> shuffledCopy(final Map<String,
            List<ClothingArticle>> original) {
        Map<String, List<ClothingArticle>> copy = new HashMap<>();
        for (var e : original.entrySet()) {
            List<ClothingArticle> list = new ArrayList<>(e.getValue());
            Collections.shuffle(list);
            copy.put(e.getKey(), list);
        }
        return copy;
    }

    private boolean isSameTopOrBottom(final Outfit outfit,
                                      final Set<String> previousNames) {
        if (outfit == null || previousNames == null) {
            return false;
        }
        var items = outfit.getItems();
        if (items == null) {
            return false;
        }
        ClothingArticle top = items.get("top");
        ClothingArticle bottom = items.get("bottom");
        return (top != null && previousNames.contains(top.getName()))
                &&
                (bottom != null && previousNames.contains(bottom.getName()));
    }


    private Set<String> extractNames(final Outfit outfit) {
        Set<String> names = new HashSet<>();
        if (outfit != null && outfit.getItems() != null) {
            for (ClothingArticle a : outfit.getItems().values()) {
                if (a != null && a.getName() != null) {
                    names.add(a.getName());
                }
            }
        }
        return names;
    }
}