package four.com.placesrecommendationsonheremap.db.entities;

import com.stfalcon.chatkit.commons.models.IUser;

public class Author implements IUser {

    private String id;
    private String name;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return "avatar";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
