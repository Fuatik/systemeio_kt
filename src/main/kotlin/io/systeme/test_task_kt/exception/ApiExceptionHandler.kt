package io.systeme.test_task_kt.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

@RestControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidExceptions(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ProblemDetail> {
        val errors = ex.bindingResult.allErrors
        val defaultMessage = errors.lastOrNull()?.defaultMessage ?: "Unknown error"

        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, defaultMessage)
        problemDetail.type = URI.create("about:blank")
        problemDetail.title = "Bad Request"
        problemDetail.instance = URI.create((request as ServletWebRequest).request.requestURI)
        return ResponseEntity(problemDetail, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestExceptions(ex: BadRequestException, request: WebRequest): ResponseEntity<ProblemDetail> {
        val problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Unknown error", request)
        return ResponseEntity(problemDetail, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<ProblemDetail> {
        val problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Unknown error", request)
        return ResponseEntity(problemDetail, HttpStatus.BAD_REQUEST)
    }

    private fun createProblemDetail(status: HttpStatus, message: String, request: WebRequest): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, message)
        problemDetail.type = URI.create("about:blank")
        problemDetail.title = status.reasonPhrase
        problemDetail.instance = URI.create((request as ServletWebRequest).request.requestURI)
        return problemDetail
    }
}