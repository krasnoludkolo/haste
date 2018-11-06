package io.haste;

import java.time.LocalDateTime;

public interface TimeSource {

    LocalDateTime now();

}
