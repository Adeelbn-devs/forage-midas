package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = new RestTemplate(); // API ko call lagane wala tool
    }

    public void save(UserRecord userRecord) {
        if (userRecord != null) {
            userRepository.save(userRecord);
        }
    }

    public void processTransaction(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender != null && recipient != null) {
            if (sender.getBalance() >= transaction.getAmount()) {
                
                // 1. External Incentive API ko transaction bhejo aur bonus lo
                Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);
                float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

                // 2. Balance Update karein (Recipient ko transaction amount + incentive dono milenge)
                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

                // 3. Database mein Users save karein
                userRepository.save(sender);
                userRepository.save(recipient);

                // 4. Naya Transaction Record banayein aur save karein
                TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
                transactionRepository.save(record);

                // --- HACK --- 
                if (sender.getName().equals("wilbur")) {
                    System.out.println("===> WILBUR BALANCE: " + sender.getBalance());
                }
                if (recipient.getName().equals("wilbur")) {
                    System.out.println("===> WILBUR BALANCE: " + recipient.getBalance());
                }
            }
        }
    }
}