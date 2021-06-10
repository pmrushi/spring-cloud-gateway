package com.example.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cloud.gateway.actuate.AbstractGatewayControllerEndpoint;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@RestControllerEndpoint(id = "services")
public class ServicesHealthIndicator extends AbstractGatewayControllerEndpoint {

    private RestTemplate restTemplate = new RestTemplate();

    public ServicesHealthIndicator(RouteDefinitionLocator routeDefinitionLocator, List<GlobalFilter> globalFilters, List<GatewayFilterFactory> gatewayFilters, List<RoutePredicateFactory> routePredicates, RouteDefinitionWriter routeDefinitionWriter, RouteLocator routeLocator) {
        super(routeDefinitionLocator, globalFilters, gatewayFilters, routePredicates, routeDefinitionWriter, routeLocator);
    }

    @GetMapping("/status")
    public @ResponseBody ResponseEntity servicesHealthCheck() {
        List<ServiceStatus> serviceStatuses = getServicesHealth(configuredRoutes());
        return new ResponseEntity<>(serviceStatuses, HttpStatus.OK);
    }

    @GetMapping("/status/all")
    public @ResponseBody ResponseEntity allServicesConsolidatedHealthCheck() {
        ServiceStatus consolidatedHealth = getAllServicesConsolidatedHealth(configuredRoutes());
        return new ResponseEntity<>(consolidatedHealth, HttpStatus.OK);
    }

    private List<Route> configuredRoutes() {
        Mono<List<Route>> routeLocators = this.routeLocator.getRoutes().collectList();
        List<Route> routesList = new ArrayList<>();
        routeLocators.subscribe(routes -> routesList.addAll(routes));
        return routesList;
    }

    private ServiceStatus getAllServicesConsolidatedHealth(List<Route> routesList) {
        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.status = Status.UP.toString();
        routesList.forEach(route -> {
            URI uri = route.getUri();
            ServiceStatus status = getStatus(uri);
            if (status.status.equals(Status.DOWN.toString())) {
                serviceStatus.status = Status.DOWN.toString();
                return;
            }
        });
        return serviceStatus;
    }

    private List<ServiceStatus> getServicesHealth(List<Route> routesList) {
        List<ServiceStatus> servicesHealth = new ArrayList<>();
        routesList.forEach(route -> {
            URI uri = route.getUri();
            servicesHealth.add(getStatus(uri));
        });
        return servicesHealth;
    }

    private ServiceStatus getStatus(URI uri) {
        try {
            String serviceHealthURL = uri + "/actuator/health";
            ServiceStatus serviceStatus = restTemplate.getForObject(serviceHealthURL, ServiceStatus.class);
            serviceStatus.service = uri.toString();
            return serviceStatus;
        } catch (Exception e) {
            return new ServiceStatus(uri.toString(), Status.DOWN.getCode());
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ServiceStatus {
        public String service;
        public String status;
        ServiceStatus() {

        }
        ServiceStatus(String service, String status) {
            this.service = service;
            this.status = status;
        }
    }
}
