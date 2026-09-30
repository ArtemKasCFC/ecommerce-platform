package com.petproject.ecommerce.infrastructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KafkaContainerManager {

    private static final List<String> DOCKER_COMPOSE_COMMAND = List.of("docker", "compose", "--env-file", ".env", "-f", "docker/docker-compose.yml");
    private static final String SERVICE = "kafka";

    public static void stopKafka() throws IOException, InterruptedException {
        executeDockerCommand("stop", SERVICE);
    }

    public static void startKafka() throws IOException, InterruptedException {
        executeDockerCommand("start", SERVICE);
    }

    private static void executeDockerCommand(String action, String service) throws IOException, InterruptedException {

        List<String> command = new ArrayList<>(DOCKER_COMPOSE_COMMAND);
        command.add(action);
        command.add(service);

        Process process = new ProcessBuilder(command).start();
        process.waitFor();
    }
}
