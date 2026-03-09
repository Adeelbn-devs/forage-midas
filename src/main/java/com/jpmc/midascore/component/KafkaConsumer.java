package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    // Yahan humne topic ka naam hardcode kar diya hai fix karne ke liye
    @KafkaListener(topics = "trader-updates", groupId = "midas-group")
    public void listen(Transaction transaction) {
        
        System.out.println("Received Transaction Amount: " + transaction.getAmount());
        
        logger.info("Received Transaction: {}", transaction);
    }
}