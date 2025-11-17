package FuzeWardrobePlanner;

public class WeatherDay {

    private String weather;      // 天气描述（如 Sunny、Rainy）
    private int temperature;     // 温度
    private Outfit outfit;       // 当天推荐的 Outfit
    private String location;     // 地点
    private String date;         // 日期（string 格式即可）

    public WeatherDay(String weather, int temperature, Outfit outfit, String location, String date) {
        this.weather = weather;
        this.temperature = temperature;
        this.outfit = outfit;
        this.location = location;
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public Outfit getOutfit() {
        return outfit;
    }

    public void setOutfit(Outfit outfit) {
        this.outfit = outfit;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "WeatherDay{" +
                "weather='" + weather + '\'' +
                ", temperature=" + temperature +
                ", outfit=" + outfit +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
