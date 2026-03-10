package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final DatabaseConduit databaseConduit;

    public KafkaConsumer(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @KafkaListener(topics = "trader-updates", groupId = "midas-group")
    public void listen(Transaction transaction) {
        // Ab hum print karne ke bajaye database function ko bhej rahe hain
        databaseConduit.processTransaction(transaction);
    }
}