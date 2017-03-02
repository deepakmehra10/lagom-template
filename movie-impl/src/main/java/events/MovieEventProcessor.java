package events;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import events.MovieEvent.*;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Created by knoldus on 31/1/17.
 */
public class MovieEventProcessor extends ReadSideProcessor<MovieEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieEventProcessor.class);

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement writeMovies;
    private PreparedStatement deleteMovies;

    @Inject
    public MovieEventProcessor(final CassandraSession session, final CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    @Override
    public PSequence<AggregateEventTag<MovieEvent>> aggregateTags() {
        LOGGER.info(" aggregateTags method ... ");
        return TreePVector.singleton(MovieEventTag.INSTANCE);
    }

    @Override
    public ReadSideHandler<MovieEvent> buildHandler() {
        LOGGER.info(" buildHandler method ... ");
        return readSide.<MovieEvent>builder("Movies_offset")
                .setGlobalPrepare(this::createTable)
                .setPrepare(evtTag -> prepareWriteMovie()
                        .thenCombine(prepareDeleteMovie(), (d1, d2) -> Done.getInstance())
                )
                .setEventHandler(MovieCreated.class, this::processPostAdded)
                .setEventHandler(MovieUpdated.class, this::processPostUpdated)
                .setEventHandler(MovieDeleted.class, this::processPostDeleted)
                .build();
    }

    // Execute only once while application is start
    private CompletionStage<Done> createTable() {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS Movies ( " +
                        "id TEXT, name TEXT, genre TEXT, PRIMARY KEY(id))"
        );
    }

    /*
    * START: Prepare statement for insert Movie values into Movies table.
    * This is just creation of prepared statement, we will map this statement with our event
    */
    private CompletionStage<Done> prepareWriteMovie() {
        return session.prepare(
                "INSERT INTO Movies (id, name, genre) VALUES (?, ?, ?)"
        ).thenApply(ps -> {
            setWriteMovies(ps);
            return Done.getInstance();
        });
    }

    private void setWriteMovies(PreparedStatement statement) {
        this.writeMovies = statement;
    }

    // Bind prepare statement while MovieCreate event is executed
    private CompletionStage<List<BoundStatement>> processPostAdded(MovieCreated event) {
        BoundStatement bindWriteMovie = writeMovies.bind();
        bindWriteMovie.setString("id", event.getMovie().getId());
        bindWriteMovie.setString("name", event.getMovie().getName());
        bindWriteMovie.setString("genre", event.getMovie().getGenre());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteMovie));
    }
    /* ******************* END ****************************/

    /* START: Prepare statement for update the data in Movies table.
    * This is just creation of prepared statement, we will map this statement with our event
    */
    private CompletionStage<List<BoundStatement>> processPostUpdated(MovieUpdated event) {
        BoundStatement bindWriteMovie = writeMovies.bind();
        bindWriteMovie.setString("id", event.getMovie().getId());
        bindWriteMovie.setString("name", event.getMovie().getName());
        bindWriteMovie.setString("genre", event.getMovie().getGenre());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteMovie));
    }
    /* ******************* END ****************************/

    /* START: Prepare statement for delete the the Movie from table.
    * This is just creation of prepared statement, we will map this statement with our event
    */
    private CompletionStage<Done> prepareDeleteMovie() {
        return session.prepare(
                "DELETE FROM Movies WHERE id=?"
        ).thenApply(ps -> {
            setDeleteMovies(ps);
            return Done.getInstance();
        });
    }

    private void setDeleteMovies(PreparedStatement deleteMovies) {
        this.deleteMovies = deleteMovies;
    }

    private CompletionStage<List<BoundStatement>> processPostDeleted(MovieDeleted event) {
        BoundStatement bindWriteMovie = deleteMovies.bind();
        bindWriteMovie.setString("id", event.getMovie().getId());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteMovie));
    }
    /* ******************* END ****************************/
}
