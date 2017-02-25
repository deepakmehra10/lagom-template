import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import states.MovieStates;

import java.time.LocalDateTime;
import java.util.Optional;

public class MovieEntity  extends PersistentEntity<MovieCommand, MovieEvent, MovieState> {

    @Override
    public Behavior initialBehavior(Optional<MovieStates> snapshotState) {

        // initial behaviour of user
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(
                MovieState.builder().user(Optional.empty())
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(CreateMovie.class, (cmd, ctx) ->
                ctx.thenPersist(MovieCreated.builder().user(cmd.getMovie())
                        .entityId(entityId()).build(), evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(MovieCreated.class, evt ->
                MovieState.builder().user(Optional.of(evt.getMovie()))
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(UpdateMovie.class, (cmd, ctx) ->
                ctx.thenPersist(MovieUpdated.builder().user(cmd.getMovie()).entityId(entityId()).build()
                        , evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(MovieUpdated.class, evt ->
                MovieState.builder().user(Optional.of(evt.getMovie()))
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(DeleteMovie.class, (cmd, ctx) ->
                ctx.thenPersist(MovieDeleted.builder().user(cmd.getMovie()).entityId(entityId()).build(),
                        evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(MovieDeleted.class, evt ->
                MovieState.builder().user(Optional.empty())
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setReadOnlyCommandHandler(MovieCurrentState.class, (cmd, ctx) ->
                ctx.reply(state().getMovie())
        );

        return behaviorBuilder.build();
    }
}
