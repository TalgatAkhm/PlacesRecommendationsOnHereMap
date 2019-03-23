package four.com.placesrecommendationsonheremap.db.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;



@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerPlace {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("address")
    private String address;
    @JsonProperty("timetable")
    private String timetable;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("is_stub")
    private Boolean isStub;
    @JsonProperty("description")
    private String description;
    @JsonProperty("site_url")
    private String siteUrl;
    @JsonProperty("foreign_url")
    private String foreignUrl;
    @JsonProperty("coords")
    private Coords coords;
    @JsonProperty("subway")
    private String subway;
    @JsonProperty("favorites_count")
    private Integer favoritesCount;
    @JsonProperty("is_closed")
    private Boolean isClosed;
    @JsonProperty("categories")
    private List<String> categories = null;
    @JsonProperty("short_title")
    private String shortTitle;
    @JsonProperty("tags")
    private List<String> tags = null;
    @JsonProperty("location")
    private String location;
    @JsonProperty("noise_level")
    private Double noiseLevel;
    @JsonProperty("mood1")
    private String mood1;
    @JsonProperty("mood2")
    private String mood2;
    @JsonProperty("mood3")
    private String mood3;
    @JsonProperty("occupancy")
    private Double occupancy;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("slug")
    public String getSlug() {
        return slug;
    }

    @JsonProperty("slug")
    public void setSlug(String slug) {
        this.slug = slug;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("timetable")
    public String getTimetable() {
        return timetable;
    }

    @JsonProperty("timetable")
    public void setTimetable(String timetable) {
        this.timetable = timetable;
    }

    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    @JsonProperty("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("is_stub")
    public Boolean getIsStub() {
        return isStub;
    }

    @JsonProperty("is_stub")
    public void setIsStub(Boolean isStub) {
        this.isStub = isStub;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("site_url")
    public String getSiteUrl() {
        return siteUrl;
    }

    @JsonProperty("site_url")
    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    @JsonProperty("foreign_url")
    public String getForeignUrl() {
        return foreignUrl;
    }

    @JsonProperty("foreign_url")
    public void setForeignUrl(String foreignUrl) {
        this.foreignUrl = foreignUrl;
    }

    @JsonProperty("coords")
    public Coords getCoords() {
        return coords;
    }

    @JsonProperty("coords")
    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    @JsonProperty("subway")
    public String getSubway() {
        return subway;
    }

    @JsonProperty("subway")
    public void setSubway(String subway) {
        this.subway = subway;
    }

    @JsonProperty("favorites_count")
    public Integer getFavoritesCount() {
        return favoritesCount;
    }

    @JsonProperty("favorites_count")
    public void setFavoritesCount(Integer favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    @JsonProperty("is_closed")
    public Boolean getIsClosed() {
        return isClosed;
    }

    @JsonProperty("is_closed")
    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    @JsonProperty("categories")
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @JsonProperty("short_title")
    public String getShortTitle() {
        return shortTitle;
    }

    @JsonProperty("short_title")
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("noise_level")
    public Double getNoiseLevel() {
        return noiseLevel;
    }

    @JsonProperty("noise_level")
    public void setNoiseLevel(Double noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    @JsonProperty("mood1")
    public String getMood1() {
        return mood1;
    }

    @JsonProperty("mood1")
    public void setMood1(String mood1) {
        this.mood1 = mood1;
    }

    @JsonProperty("mood2")
    public String getMood2() {
        return mood2;
    }

    @JsonProperty("mood2")
    public void setMood2(String mood2) {
        this.mood2 = mood2;
    }

    @JsonProperty("mood3")
    public String getMood3() {
        return mood3;
    }

    @JsonProperty("mood3")
    public void setMood3(String mood3) {
        this.mood3 = mood3;
    }

    @JsonProperty("occupancy")
    public Double getOccupancy() {
        return occupancy;
    }

    @JsonProperty("occupancy")
    public void setOccupancy(Double occupancy) {
        this.occupancy = occupancy;
    }

}