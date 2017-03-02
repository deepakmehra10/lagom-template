package com.knoldus;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.sun.rowset.internal.Row;

import java.util.List;
import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;
import static com.lightbend.lagom.javadsl.api.transport.Method.*;

public interface MovieService extends Service{
    ServiceCall<NotUsed, Optional<Movie>> movie(String id);

    ServiceCall<Movie, Done> newMovie();

    ServiceCall<Movie, Done> updateMovie(String id);

    ServiceCall<NotUsed, Done> deleteMovie(String id);

    //ServiceCall<NotUsed, Optional<Movie>> getAllMovie();

    @Override
    default Descriptor descriptor() {

        return named("movie").withCalls(
                restCall(GET, "/api/movie/:id", this::movie),
                restCall(POST, "/api/new-movie", this::newMovie),
                restCall(PUT, "/api/update-movie/:id", this::updateMovie),
                restCall(DELETE, "/api/delete-movie/:id", this::deleteMovie)
              //  restCall(GET, "/api/user/get-all-movie", this::getAllMovie)
        ).withAutoAcl(true);
    }
}
