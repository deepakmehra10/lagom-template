package states;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.Movie;
import com.lightbend.lagom.serialization.CompressedJsonable;

import java.util.Optional;

@Value
@Builder
@JsonDeserialize
@AllArgsConstructor
public class MovieStates implements CompressedJsonable {
    Optional<Movie> movie;
    String timestamp;
}
