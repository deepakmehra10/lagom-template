package events;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * Created by knoldus on 31/1/17.
 */
public class MovieEventTag {

    public static final AggregateEventTag<MovieEvent> INSTANCE = AggregateEventTag.of(MovieEvent.class);
}
