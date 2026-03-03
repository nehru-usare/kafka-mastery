//package com.smartjava.kafka.common;
//
//import java.io.File;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Utility class to start all Spring Boot microservices simultaneously from a
// * single place.
// * 
// * Note: We use ProcessBuilder to run them via Maven rather than importing them
// * as Java dependencies.
// * If we imported them into common-lib directly, it would cause a "circular
// * dependency" in Maven
// * because the other services already depend on common-lib.
// */
//public class ServiceStarter {
//
//    public static void main(String[] args) {
//        String projectRoot = System.getProperty("user.dir");
//        System.out.println("Starting all Kafka microservices from root directory: " + projectRoot);
//
//        // List containing all our Spring Boot service modules
//        List<String> modules = List.of(
//                "order-service",
//                "payment-service",
//                "notification-service",
//                "analytics-service");
//
//        // Create a thread pool so we can run them all at the same time
//        ExecutorService executor = Executors.newFixedThreadPool(modules.size());
//
//        for (String module : modules) {
//            executor.submit(() -> startService(projectRoot, module));
//        }
//
//        // Add shutdown hook to help close services when stopping this app
//        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));
//    }
//
//    private static void startService(String projectRoot, String module) {
//        try {
//            System.out.println("======> Bootstrapping module: " + module + " <======");
//
//            ProcessBuilder processBuilder;
//
//            // Handle OS-level differences
//            if (System.getProperty("os.name").toLowerCase().contains("win")) {
//                processBuilder = new ProcessBuilder("cmd", "/c", "mvn", "spring-boot:run", "-pl", module);
//            } else {
//                processBuilder = new ProcessBuilder("mvn", "spring-boot:run", "-pl", module);
//            }
//
//            // Set the working directory to our project root
//            processBuilder.directory(new File(projectRoot));
//
//            // Route the output directly to our current console so we can see all the logs
//            // together
//            processBuilder.inheritIO();
//
//            Process process = processBuilder.start();
//            process.waitFor();
//
//        } catch (Exception e) {
//            System.err.println("Failed to start module: " + module);
//            e.printStackTrace();
//        }
//    }
//}
