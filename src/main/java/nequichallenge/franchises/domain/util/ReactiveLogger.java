package nequichallenge.franchises.domain.util;

import org.slf4j.Logger;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * Utilidad centralizada de logging para flujos reactivos.
 * Propaga automáticamente el correlationId desde el contexto reactivo.
 * Uso:
 * <pre>{@code
 *   .doOnError(e -> ReactiveLogger.error(log, ctx, "Error al crear sucursal", e))
 *   .doOnSuccess(r -> ReactiveLogger.info(log, ctx, "Sucursal creada exitosamente"))
 * }</pre>
 */
public final class ReactiveLogger {

    public static final String CORRELATION_ID_KEY = "correlationId";
    private static final String DEFAULT_CORRELATION_ID = "N/A";
    private static final String LOG_FORMAT = "[{}] {}";
    private static final String LOG_ERROR_FORMAT = "[{}] {} - {}";

    private ReactiveLogger() {}

    /**
     * Log a nivel INFO con correlationId extraído del contexto reactivo.
     */
    public static void info(Logger log, ContextView ctx, String message) {
        if (log.isInfoEnabled()) {
            log.info(LOG_FORMAT, extractCorrelationId(ctx), message);
        }
    }
    /**
     * Log a nivel ERROR con correlationId y mensaje de error.
     */
    public static void error(Logger log, ContextView ctx, String message, Throwable error) {
        log.error(LOG_ERROR_FORMAT, extractCorrelationId(ctx), message, error.getMessage(), error);
    }

    /**
     * Log a nivel ERROR sin Throwable.
     */
    public static void error(Logger log, ContextView ctx, String message) {
        log.error(LOG_FORMAT, extractCorrelationId(ctx), message);
    }

    /**
     * Envuelve un Mono con logging de éxito y error usando el contexto reactivo.
     * Uso con transform:
     * <pre>{@code
     *   .transform(flow -> ReactiveLogger.logResult(log, flow, "Éxito", "Error"))
     * }</pre>
     */
    public static <T> Mono<T> logResult(Logger log, Mono<T> flow, String successMsg, String errorMsg) {
        return Mono.deferContextual(ctx ->
                flow.doOnSuccess(r -> info(log, ctx, successMsg))
                    .doOnError(e -> error(log, ctx, errorMsg, e))
        );
    }

    /**
     * Extrae el correlationId del contexto reactivo.
     * Retorna "N/A" si no está presente.
     */
    private static String extractCorrelationId(ContextView ctx) {
        return ctx.getOrDefault(CORRELATION_ID_KEY, DEFAULT_CORRELATION_ID);
    }
}

