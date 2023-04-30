package liga.tinder.client.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class User {
    private Profile profile;
    private String token;
    private ScrollableListWrapper scrollableListWrapper;

    public User(Long userId) {
        this.profile = new Profile();
        profile.setUserId(userId);
    }
}
