package nequichallenge.franchises.infrastructure.input;

import nequichallenge.franchises.application.http.handler.interfaces.IProductHandler;
import nequichallenge.franchises.domain.util.ConstRoute;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRoute {
    @Bean
    public RouterFunction<ServerResponse> productRoutes(IProductHandler productHandler) {
        return route(POST(ConstRoute.PRODUCT+ConstRoute.CREATE), productHandler::createProduct);
    }
}
