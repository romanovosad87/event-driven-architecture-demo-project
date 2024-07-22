package com.study.productmicroservice.command.controller;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@RestController
@RequestMapping("/management")
public class EventsReplayController {

    @Autowired
    private EventProcessingConfiguration eventProcessingConfiguration;

    @PostMapping("/eventProcessor/{processorName}/reset")
    public ResponseEntity<String> replayEvents(@PathVariable String processorName) {
        Optional<TrackingEventProcessor> trackingEventProcessor
                = eventProcessingConfiguration.eventProcessor(processorName, TrackingEventProcessor.class);

        final ResponseEntity<String>[] responseEntity = new ResponseEntity[1];

        trackingEventProcessor.ifPresentOrElse(processor -> {
            processor.shutDown();
            processor.resetTokens();
            processor.start();
            responseEntity[0] = new ResponseEntity<>("The event processor with a name [%s] has been reset".formatted(processorName), HttpStatus.OK);
        },
                () -> responseEntity[0] = new ResponseEntity<>(("The event processor with a name [%s] is not a tracking event processor. "
                        + "Only tracking event processor is supported").formatted(processorName), HttpStatus.BAD_REQUEST));

        return responseEntity[0];
    }
}
