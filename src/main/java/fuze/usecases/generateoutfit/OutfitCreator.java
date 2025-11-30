package fuze.usecases.generateoutfit;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.entity.weather.WeatherDay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple rule-based outfit builder:
 * - requires at least a top and bottom
 * - optionally adds outer layer and accessory
 * - honors rain preference when possible (chooses waterproof items if rain)
 * - tries to match temperature using clothing weatherRating as a warmth score
 * Assumptions:
 * - wardrobe is provided as Map with keys: "top", "bottom", "outer", "accessory"
 * - clothing weatherRating is an integer warmth score (higher = warmer)
 * - the WeatherDay weather string contains "rain" (case-insensitive) if it's raining,
 *   or you can pass a boolean flag via isRaining argument.
 */
public class OutfitCreator {

    public Outfit createOutfitForDay(WeatherDay day,
                                     Map<String, List<ClothingArticle>> wardrobe,
                                     boolean isRainingOverride) {
        if (wardrobe == null) {
            throw new IllegalArgumentException("Wardrobe cannot be null");
        }

        boolean isRaining = isRainingOverride || (day != null && isRainyString(day.getWeather()));
        double averageTemperature = day != null ? averageTemp(day) : 15.0;

        /*
         * Clothing warmth rating is 0–5 (0 = lightest, 5 = warmest).
         * We map the day's average temperature (C) to this 0–5 target using simple buckets.
         * Colder temps map to higher warmth ratings (layer up).
         */
        int targetWarmth = day != null
                ? tempToWarmthRating(averageTemp(day))
                : 2; // neutral fallback

        ClothingArticle top = chooseBest(wardrobe.get("top"), targetWarmth, isRaining);
        ClothingArticle bottom = chooseBest(wardrobe.get("bottom"), targetWarmth, isRaining);

        if (top == null || bottom == null) {
            // Cannot build a valid outfit without these
            return null;
        }

        int baseWarmth = top.getWeatherRating() + bottom.getWeatherRating();
        boolean needsWarmthLayer = baseWarmth < targetWarmth;
        boolean needsRainLayer = isRaining;

        ClothingArticle outer = null;
        if ((needsWarmthLayer || needsRainLayer) && wardrobe.get("outer") != null) {
            outer = chooseOptional(wardrobe.get("outer"), targetWarmth, isRaining);
            if (outer == null && needsRainLayer) {
                outer = chooseBestWaterproof(wardrobe.get("outer"));
            }
        }

        boolean includeAccessory = targetWarmth >= 4 || averageTemperature <= 8 || isRaining;
        ClothingArticle accessory = null;
        if (includeAccessory && wardrobe.get("accessory") != null) {
            accessory = chooseOptional(wardrobe.get("accessory"), targetWarmth, isRaining);
        }

        Map<String, ClothingArticle> items = new HashMap<>();
        items.put("top", top);
        items.put("bottom", bottom);
        if (outer != null) {
            items.put("outer", outer);
        }
        if (accessory != null) {
            items.put("accessory", accessory);
        }

        String title = buildTitle(day, isRaining);
        boolean waterproof = isRaining && (hasWaterproof(top) || hasWaterproof(bottom)
                || hasWaterproof(outer) || hasWaterproof(accessory));
        int combinedWarmth = baseWarmth + (outer != null ? outer.getWeatherRating() : 0);

        return new Outfit(items, title, combinedWarmth, waterproof);
    }

    private ClothingArticle chooseBest(List<ClothingArticle> options, int targetWarmth, boolean preferWaterproof) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        ClothingArticle best = null;
        int bestScore = Integer.MAX_VALUE;
        for (ClothingArticle article : options) {
            if (article == null) continue;
            if (preferWaterproof && article.isWaterproof()) {
                // lift waterproof items when it's raining
                int score = Math.abs(article.getWeatherRating() - targetWarmth);
                if (score <= bestScore) {
                    best = article;
                    bestScore = score;
                }
            } else if (!preferWaterproof) {
                int score = Math.abs(article.getWeatherRating() - targetWarmth);
                if (score < bestScore) {
                    best = article;
                    bestScore = score;
                }
            }
        }
        // if we preferred waterproof but none exist, fallback to closest warmth
        if (best == null && preferWaterproof) {
            for (ClothingArticle article : options) {
                if (article == null) continue;
                int score = Math.abs(article.getWeatherRating() - targetWarmth);
                if (score < bestScore) {
                    best = article;
                    bestScore = score;
                }
            }
        }
        return best;
    }

    private ClothingArticle chooseOptional(List<ClothingArticle> options, int targetWarmth, boolean preferWaterproof) {
        ClothingArticle candidate = chooseBest(options, targetWarmth, preferWaterproof);
        if (candidate == null) {
            return null;
        }
        int diff = Math.abs(candidate.getWeatherRating() - targetWarmth);
        return diff <= 2 ? candidate : null;
    }


    private ClothingArticle chooseBestWaterproof(List<ClothingArticle> options) {
        if (options == null) return null;
        for (ClothingArticle article : options) {
            if (article != null && article.isWaterproof()) {
                return article;
            }
        }
        return null;
    }

    /**
     * Map temperature (C) to a 0-5 warmth rating bucket.
     * 5 = very cold, 0 = hot.
     */
    private int tempToWarmthRating(double tempC) {
        if (tempC <= -5) return 5;
        if (tempC <= 2) return 4;
        if (tempC <= 9) return 3;
        if (tempC <= 16) return 2;
        if (tempC <= 23) return 1;
        return 0;
    }

    private boolean isRainyString(String weather) {
        return weather != null && weather.toLowerCase().contains("rain");
    }

    private boolean hasWaterproof(ClothingArticle article) {
        return article != null && article.isWaterproof();
    }

    private String buildTitle(WeatherDay day, boolean isRaining) {
        String date = day != null ? day.getDate() : "Unknown date";
        String location = formatLocation(day);
        return String.format("%s outfit for %s%s", location, date, isRaining ? " (rain)" : "");
    }

    private String formatLocation(WeatherDay day) {
        if (day == null || day.getLocation() == null || day.getLocation().length < 2) {
            return "Unknown location";
        }
        double[] loc = day.getLocation();
        return String.format("%.4f, %.4f", loc[0], loc[1]);
    }

    private double averageTemp(WeatherDay day) {
        return (day.getTemperatureHigh() + day.getTemperatureLow()) / 2.0;
    }
}
