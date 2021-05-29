package grailsgenerated.controller

import org.springframework.http.HttpStatus

trait ControllerExtensions {

    void respondError(HttpStatus httpStatus, String message = null) {
        if (message == null) {
            message = httpStatus.name()
        }
        Map model = [error: httpStatus.value(), message: message]
        response.status = httpStatus.value()
        respond model
    }

}