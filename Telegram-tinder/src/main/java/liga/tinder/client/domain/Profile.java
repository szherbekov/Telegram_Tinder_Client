package liga.tinder.client.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Profile {
    private String name;
    private String password;
    private Long userId;
    private Sex sex;
    private String description;
    private Set <Sex> findSex;

}
