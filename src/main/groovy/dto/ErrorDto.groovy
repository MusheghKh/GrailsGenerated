package dto

class ErrorDto {

    Integer error
    String message

    ErrorDto(Integer error, String message) {
        this.error = error
        this.message = message
    }
}
