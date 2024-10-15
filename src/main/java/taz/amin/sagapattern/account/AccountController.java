package taz.amin.sagapattern.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import taz.amin.sagapattern.event.BalanceDeductedEvent;
import taz.amin.sagapattern.exception.InsufficientFundsException;

@RestController
@RequestMapping("/account")
public class AccountController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class.getName());
    private final StreamBridge streamBridge;

    @Autowired
    public AccountController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @PostMapping("/deduct-balance")
    public ResponseEntity<String> deductBalance(@RequestBody AccountRequest request) {
        boolean success = deductBalanceFromAccount(request.getUserId(), request.getAmount());

        if (success) {
            Message message = MessageBuilder
                    .withPayload(new BalanceDeductedEvent(BalanceDeductedEvent.Type.SUCCESS, request.getUserId(), request.getOrderId()))
                    .build();
            streamBridge.send("balanceUpdate-out-0", message);
            return ResponseEntity.ok("Balance deducted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to deduct balance.");
        }
    }

    private boolean deductBalanceFromAccount(String userId, double amount) {
        // Simulate balance deduction logic
        if(amount < 100.00)
            LOG.info("Successfully Deducted {} for user {}", amount, userId);
        else if(amount >= 100 && amount < 200)
            return false;
        else
            throw new InsufficientFundsException(String.format("Insufficient Funds for user %s", userId));
        return true;
    }
}
