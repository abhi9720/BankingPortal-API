package com.webapp.bankingportal.config;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.webapp.bankingportal.entity.Currency;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
/**
 * Seeds a base set of ISO 4217 currencies on application startup and
 * back-fills any pre-existing accounts that do not yet have a currency
 * assigned (i.e. rows that existed before multi-currency support was
 * introduced) with the configured default currency.
 */
@Slf4j
@Configuration
public class CurrencySeeder {
    private static final List<Currency> DEFAULTS = List.of(
            new Currency(null, "USD", "US Dollar",        "$"),
            new Currency(null, "EUR", "Euro",             "\u20AC"),
            new Currency(null, "GBP", "British Pound",    "\u00A3"),
            new Currency(null, "INR", "Indian Rupee",     "\u20B9"),
            new Currency(null, "JPY", "Japanese Yen",     "\u00A5"),
            new Currency(null, "AUD", "Australian Dollar","A$"),
            new Currency(null, "CAD", "Canadian Dollar",  "C$")
    );
    @Bean
    public CommandLineRunner seedCurrencies(
            CurrencyRepository currencyRepository,
            AccountRepository accountRepository,
            @Value("${app.default-currency:INR}") String defaultCurrencyCode) {
        return args -> {
            for (Currency seed : DEFAULTS) {
                currencyRepository.findByCode(seed.getCode())
                        .orElseGet(() -> {
                            log.info("Seeding currency {}", seed.getCode());
                            return currencyRepository.save(seed);
                        });
            }
            Currency defaultCurrency = currencyRepository.findByCode(defaultCurrencyCode)
                    .orElseGet(() -> currencyRepository.findByCode("INR").orElse(null));
            if (defaultCurrency == null) {
                log.warn("Default currency '{}' could not be resolved; skipping back-fill",
                        defaultCurrencyCode);
                return;
            }
            // Back-fill any legacy accounts created before multi-currency support.
            accountRepository.findAll().stream()
                    .filter(account -> account.getCurrency() == null)
                    .forEach(account -> {
                        account.setCurrency(defaultCurrency);
                        accountRepository.save(account);
                        log.info("Back-filled currency {} for account {}",
                                defaultCurrency.getCode(), account.getAccountNumber());
                    });
        };
    }
}
