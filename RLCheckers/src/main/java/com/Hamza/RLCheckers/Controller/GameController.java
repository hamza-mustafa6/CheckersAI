package com.Hamza.RLCheckers.Controller;
import com.Hamza.RLCheckers.CheckersLogic.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@RestController
@RequestMapping(path = "api/start")
public class GameController {
    
    private final ExecutorService executor = Executors.newCachedThreadPool();


/*Controller has a getboard method */

    @GetMapping("/stream-sse")
    public SseEmitter streamSseEvents() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        executor.execute(() -> {
            try {
                // RL game = new RL(0.99, 0.05, 0.01);
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000); // simulate delay
                    emitter.send("SSE MVC - " + System.currentTimeMillis());
                }
                emitter.complete();
                
            } catch (Exception e) {
                emitter.completeWithError(e);
            }


        }); 
        return emitter;
    }


    private void send(SseEmitter emitter, String eventName, Object data) {           
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)                                                        
                    .data(data));                                                           
        } catch (IOException e) {
            emitter.completeWithError(e);                                                   
        }
    }


}
