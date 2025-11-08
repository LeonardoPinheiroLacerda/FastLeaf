package br.com.leonardo.server;

import br.com.leonardo.annotation.scanner.EndpointScanner;
import br.com.leonardo.router.context.HttpEndpointContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ServerRunner {

    public static void serve(Class<?> clazz) {

        final HttpEndpointContext context = new HttpEndpointContext();

        EndpointScanner scanner = new EndpointScanner(context);
        scanner.scan(clazz);

        try (Server server = new Server(context)){
            server.start();
        } catch (IOException e) {
            log.error("Failed to start server", e);
        }

    }
}
