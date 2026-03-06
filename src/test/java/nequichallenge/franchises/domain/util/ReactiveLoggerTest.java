package nequichallenge.franchises.domain.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReactiveLoggerTest {

    private Logger log;

    @BeforeEach
    void setUp() {
        log = mock(Logger.class);
        when(log.isInfoEnabled()).thenReturn(true);
    }

    // ==================== info ====================

    @Test
    @DisplayName("info — debe loguear con correlationId del contexto")
    void info_logsWithCorrelationIdFromContext() {
        reactor.util.context.ContextView ctx = Context.of(ReactiveLogger.CORRELATION_ID_KEY, "req-123");

        ReactiveLogger.info(log, ctx, "operación exitosa");

        verify(log).info("[{}] {}", "req-123", "operación exitosa");
    }

    @Test
    @DisplayName("info — debe usar N/A cuando no hay correlationId en el contexto")
    void info_usesDefaultWhenNoCorrelationId() {
        reactor.util.context.ContextView ctx = Context.empty();

        ReactiveLogger.info(log, ctx, "sin correlación");

        verify(log).info("[{}] {}", "N/A", "sin correlación");
    }

    @Test
    @DisplayName("info — no loguea cuando isInfoEnabled es false")
    void info_doesNotLogWhenInfoDisabled() {
        when(log.isInfoEnabled()).thenReturn(false);
        reactor.util.context.ContextView ctx = Context.empty();

        ReactiveLogger.info(log, ctx, "mensaje ignorado");

        verify(log, never()).info(anyString(), any(), any());
    }

    // ==================== error con Throwable ====================

    @Test
    @DisplayName("error(Throwable) — debe loguear con correlationId y mensaje de excepción")
    void error_withThrowable_logsWithCorrelationIdAndExceptionMessage() {
        reactor.util.context.ContextView ctx = Context.of(ReactiveLogger.CORRELATION_ID_KEY, "req-456");
        RuntimeException ex = new RuntimeException("algo falló");

        ReactiveLogger.error(log, ctx, "error en operación", ex);

        verify(log).error("[{}] {} - {}", "req-456", "error en operación", "algo falló", ex);
    }

    @Test
    @DisplayName("error(Throwable) — usa N/A cuando no hay correlationId")
    void error_withThrowable_usesDefaultWhenNoCorrelationId() {
        reactor.util.context.ContextView ctx = Context.empty();
        RuntimeException ex = new RuntimeException("fallo");

        ReactiveLogger.error(log, ctx, "error", ex);

        verify(log).error("[{}] {} - {}", "N/A", "error", "fallo", ex);
    }

    // ==================== error sin Throwable ====================

    @Test
    @DisplayName("error(sin Throwable) — debe loguear solo mensaje con correlationId")
    void error_withoutThrowable_logsWithCorrelationId() {
        reactor.util.context.ContextView ctx = Context.of(ReactiveLogger.CORRELATION_ID_KEY, "req-789");

        ReactiveLogger.error(log, ctx, "mensaje de error simple");

        verify(log).error("[{}] {}", "req-789", "mensaje de error simple");
    }

    @Test
    @DisplayName("error(sin Throwable) — usa N/A cuando no hay correlationId")
    void error_withoutThrowable_usesDefaultWhenNoCorrelationId() {
        reactor.util.context.ContextView ctx = Context.empty();

        ReactiveLogger.error(log, ctx, "error sin contexto");

        verify(log).error("[{}] {}", "N/A", "error sin contexto");
    }

    // ==================== logResult ====================

    @Test
    @DisplayName("logResult — debe loguear info al completar exitosamente")
    void logResult_logsInfoOnSuccess() {
        Mono<String> flow = Mono.just("resultado");

        Mono<String> result = Mono.just("ignorado")
                .flatMap(v -> flow)
                .transform(f -> ReactiveLogger.logResult(log, f, "éxito", "error"))
                .contextWrite(Context.of(ReactiveLogger.CORRELATION_ID_KEY, "req-001"));

        StepVerifier.create(result)
                .expectNext("resultado")
                .verifyComplete();

        verify(log).info("[{}] {}", "req-001", "éxito");
        verify(log, never()).error(anyString(), any(), any(), any(Throwable.class));
    }

    @Test
    @DisplayName("logResult — debe loguear error cuando el flujo falla")
    void logResult_logsErrorOnFailure() {
        RuntimeException ex = new RuntimeException("fallo de negocio");
        Mono<String> flow = Mono.error(ex);

        Mono<String> result = ReactiveLogger.logResult(log, flow, "éxito", "error al procesar")
                .contextWrite(Context.of(ReactiveLogger.CORRELATION_ID_KEY, "req-002"));

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(log).error("[{}] {} - {}", "req-002", "error al procesar", "fallo de negocio", ex);
        verify(log, never()).info(anyString(), any(), any());
    }

    @Test
    @DisplayName("logResult — usa N/A como correlationId cuando no hay contexto")
    void logResult_usesDefaultCorrelationIdWhenNoContext() {
        Mono<String> flow = Mono.just("ok");

        Mono<String> result = ReactiveLogger.logResult(log, flow, "completado", "error");

        StepVerifier.create(result)
                .expectNext("ok")
                .verifyComplete();

        verify(log).info("[{}] {}", "N/A", "completado");
    }

    @Test
    @DisplayName("logResult — no loguea success cuando el Mono está vacío")
    void logResult_doesNotLogSuccessWhenEmpty() {
        Logger spyLog = mock(Logger.class);
        when(spyLog.isInfoEnabled()).thenReturn(true);

        Mono<String> flow = Mono.empty();

        Mono<String> result = ReactiveLogger.logResult(spyLog, flow, "éxito", "error");

        StepVerifier.create(result)
                .verifyComplete();

        // doOnSuccess se dispara con null cuando el Mono completa vacío
        verify(spyLog).info("[{}] {}", "N/A", "éxito");
    }
}

