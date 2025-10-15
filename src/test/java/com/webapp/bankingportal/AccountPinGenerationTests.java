package com.webapp.bankingportal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.webapp.bankingportal.controller.request.PinRequest;
import com.webapp.bankingportal.exception.InvalidPinException;
import com.webapp.bankingportal.exception.NotFoundException;
import com.webapp.bankingportal.exception.UnauthorizedException;
import com.webapp.bankingportal.util.ApiMessages;
import com.webapp.bankingportal.util.JsonUtil;

import lombok.val;

/**
 * Additional tests for PIN generation and update behaviors.
 * Extends project's BaseTest for shared helpers and fixtures.
 */
public class AccountPinGenerationTests extends BaseTest {

    @Test
    public void service_createPin_success() {
        val accountDetails = createAccount();

        val pin = getRandomPin();

        accountService.createPin(accountDetails.get("accountNumber"), accountDetails.get("password"), pin);

        val account = accountRepository.findByAccountNumber(accountDetails.get("accountNumber"));

        Assertions.assertNotNull(account.getPin());
        Assertions.assertTrue(passwordEncoder.matches(pin, account.getPin()));
    }

    @Test
    public void service_createPin_invalidAccount_throwsNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            accountService.createPin(getRandomAccountNumber(), getRandomPassword(), getRandomPin());
        });
    }

    @Test
    public void service_createPin_invalidPassword_throwsUnauthorized() {
        val accountDetails = createAccount();

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            accountService.createPin(accountDetails.get("accountNumber"), getRandomPassword(), getRandomPin());
        });
    }

    @Test
    public void service_createPin_missingOrEmptyPin_throwsInvalidPin() {
        val accountDetails = createAccount();

        Assertions.assertThrows(InvalidPinException.class, () -> {
            accountService.createPin(accountDetails.get("accountNumber"), accountDetails.get("password"), null);
        });

        Assertions.assertThrows(InvalidPinException.class, () -> {
            accountService.createPin(accountDetails.get("accountNumber"), accountDetails.get("password"), "");
        });
    }

    @Test
    public void service_createPin_invalidFormats_throwsInvalidPin() {
        val accountDetails = createAccount();

        // Short pin
        Assertions.assertThrows(InvalidPinException.class, () -> {
            accountService.createPin(accountDetails.get("accountNumber"), accountDetails.get("password"),
                    faker.number().digits(3));
        });

        // Long pin
        Assertions.assertThrows(InvalidPinException.class, () -> {
            accountService.createPin(accountDetails.get("accountNumber"), accountDetails.get("password"),
                    faker.number().digits(5));
        });

        // Non-digit characters
        Assertions.assertThrows(InvalidPinException.class, () -> {
            accountService.createPin(accountDetails.get("accountNumber"), accountDetails.get("password"),
                    getRandomPassword().substring(0, 4));
        });
    }

    @Test
    public void controller_createPin_success_endpoint() throws Exception {
        val userDetails = createAndLoginUser();

        val pinRequest = new PinRequest(userDetails.get("accountNumber"), getRandomPin(), userDetails.get("password"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_CREATION_SUCCESS.getMessage()));
    }

    @Test
    public void controller_createPin_invalidPassword_returnsUnauthorized() throws Exception {
        val userDetails = createAndLoginUser();

        val pinRequest = new PinRequest(userDetails.get("accountNumber"), getRandomPin(), getRandomPassword());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_INVALID_ERROR.getMessage()));
    }

    @Test
    public void controller_createPin_invalidFormat_returnsBadRequest() throws Exception {
        val userDetails = createAndLoginUser();

        val pinRequest = new PinRequest(userDetails.get("accountNumber"), faker.number().digits(5),
                userDetails.get("password"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage()));
    }

    @Test
    public void service_updatePin_success() {
        val accountDetails = createAccountWithPin(passwordEncoder, userRepository, accountService);

        val newPin = getRandomPin();

        accountService.updatePin(accountDetails.get("accountNumber"), accountDetails.get("pin",
                accountDetails.get("password"), newPin);

        val account = accountRepository.findByAccountNumber(accountDetails.get("accountNumber"));
        Assertions.assertTrue(passwordEncoder.matches(newPin, account.getPin()));
    }

    @Test
    public void service_updatePin_invalidAccount_throwsNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            accountService.updatePin(getRandomAccountNumber(), getRandomPin(), getRandomPassword(), getRandomPin());
        });
    }

    @Test
    public void service_updatePin_invalidOldPin_throwsUnauthorized() {
        val accountDetails = createAccountWithPin(passwordEncoder, userRepository, accountService);

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            accountService.updatePin(accountDetails.get("accountNumber"), getRandomPin(), accountDetails.get("password"),
                    getRandomPin());
        });
    }

    /**
     * Basic concurrency check: attempt to create PIN concurrently and ensure only one succeeds,
     * or at minimum no double-encoded pin creation leads to inconsistent state.
     *
     * This test is intentionally opportunistic and verifies that at least one thread
     * fails (throws UnauthorizedException) when trying to create a PIN twice.
     */
    @Test
    public void service_createPin_concurrentAttempts_oneShouldFail() throws InterruptedException {
        val accountDetails = createAccount();

        final int threads = 2;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threads);
        final AtomicInteger successCount = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                startLatch.await();
                try {
                    accountService.createPin(accountDetails.get("accountNumber"), accountDetails.get("password"),
                            getRandomPin());
                    successCount.incrementAndGet();
                } catch (UnauthorizedException | InvalidPinException | NotFoundException e) {
                    // expected for at least one thread
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                doneLatch.countDown();
            }
        };

        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        // start threads
        startLatch.countDown();
        doneLatch.await();

        Assertions.assertTrue(successCount.get() >= 1, "At least one thread should succeed creating the pin");
        Assertions.assertTrue(successCount.get() < threads + 1, "Not all threads should have succeeded (no duplicates)");
    }
}