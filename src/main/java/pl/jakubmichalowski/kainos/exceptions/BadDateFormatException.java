package pl.jakubmichalowski.kainos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Jakub Michałowski on 03.06.2018.
 * All rights reserved.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Wrong date format")
public class BadDateFormatException extends RuntimeException {
}
