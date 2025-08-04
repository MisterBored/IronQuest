package com.darrenswhite.rs.ironquest.path;

import com.darrenswhite.rs.ironquest.path.algorithm.PathFinderAlgorithm;
import com.darrenswhite.rs.ironquest.quest.Quest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when {@link PathFinderAlgorithm} fails to find the next optimal {@link Quest} to
 * be completed.
 *
 * @author Darren S. White
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class QuestNotFoundException extends Exception {

  public QuestNotFoundException(String message) {
    super(message);
  }
}
