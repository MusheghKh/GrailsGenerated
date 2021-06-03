package grailsgenerated.controller

import dto.ErrorDto
import org.springframework.http.HttpStatus

trait ControllerExtensions {

    void respondError(HttpStatus httpStatus, String message = null) {
        if (message == null) {
            message = httpStatus.name()
        }
        ErrorDto errorDto = new ErrorDto(httpStatus.value(), message)
        response.status = httpStatus.value()
        respond errorDto
    }

}