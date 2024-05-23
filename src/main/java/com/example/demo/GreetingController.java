package com.example.demo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@RestController
@RequestMapping("/pod")
public class GreetingController {
    String errorMessage;

    @GetMapping("/{id}/repo/{account}")
    public ResponseEntity<String> getRepository(@PathVariable String id, @PathVariable String account) {
        try {
            executeCommand("/bin/sh /repo.sh");
            String token = Files.readString(Path.of("token"), StandardCharsets.UTF_8);
            Pod pod = new Pod();
            pod.setId(id);
            pod.setAccountId(account);
            pod.setToken(token);
            savePod(pod);
            return ResponseEntity.status(HttpStatus.OK).body("Repo successfully cloned");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getPodItem(@PathVariable String id) {
        try {
            Pod pod = getPod(id);
            System.out.println("Item retrieved:");
            System.out.println(pod);
            return ResponseEntity.status(HttpStatus.OK).body("Pod successfully retrieved");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void savePod(Pod pod) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        mapper.save(pod);
    }

    private Pod getPod(String id) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        return mapper.load(Pod.class, id);
    }


    private void executeCommand(String command) {
        try {
            log(command);
            Process process = Runtime.getRuntime().exec(command);
            logOutput(process.getInputStream(), "");
            logOutput(process.getErrorStream(), "");
            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        if (errorMessage != null) {
            throw new RuntimeException(errorMessage);
        }
    }

    private void logOutput(InputStream inputStream, String prefix) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(inputStream, "UTF-8");
                while (scanner.hasNextLine()) {
                    synchronized (this) {
                        String message = scanner.nextLine();
                        log(prefix + message);
                        if (message.contains("Error") || (message.contains("ERROR"))) {
                            throw new RuntimeException(message);
                        }
                    }
                }
                scanner.close();
            }
        });
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                errorMessage = e.getMessage();
            }
        });
        t.start();
    }

    private synchronized void log(String message) {
        System.out.println(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss:SSS").format(new Date()) + ": " + message);
    }
}


