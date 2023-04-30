package liga.tinder.client.service;


import liga.tinder.client.domain.Profile;

import java.io.File;

public interface ImageService {
    File getFile(Profile user);
}
