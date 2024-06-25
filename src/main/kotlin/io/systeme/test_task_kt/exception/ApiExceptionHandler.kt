package io.systeme.test_task_kt.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import java.net.URI

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidExceptions(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ProblemDetail> {
        val errors = ex.bindingResult.allErrors
        val detailedErrors = errors.map {
            when (it) {
                is FieldError -> it.field + ": " + it.defaultMessage
                else -> it.defaultMessage
            }
        }

        val problemDetail = createProblemDetail(
            Exception(if (detailedErrors.isNotEmpty()) detailedErrors.joinToString("\n") else "Unknown error"),
            request
        )
        return ResponseEntity(problemDetail, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestExceptions(ex: Exception, request: WebRequest): ResponseEntity<ProblemDetail> {
        return ResponseEntity(createProblemDetail(ex, request), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<ProblemDetail> {
        return ResponseEntity(createProblemDetail(ex, request), HttpStatus.BAD_REQUEST)
    }

    private fun createProblemDetail(ex: Exception, request: WebRequest): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.message?.substringAfter(':')
        )
        problemDetail.type = URI.create("about:blank")
        problemDetail.title = "Bad Request"

        val servletRequest = request as? ServletWebRequest
        servletRequest?.let {
            problemDetail.instance = URI.create(it.request.requestURI)
        }

        return problemDetail
    }

}