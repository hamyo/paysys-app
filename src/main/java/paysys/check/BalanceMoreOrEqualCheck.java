package paysys.check;

import lombok.NonNull;
import paysys.domain.Account;
import paysys.repository.AccountRepository;

import java.math.BigDecimal;

/**
 * Check that the balance is greater than or equal to a value
 */
public class BalanceMoreOrEqualCheck {
    /**
     * Repository of accounts
     */
    private AccountRepository accountRepository;

    /**
     * Constructor fo check
     *
     * @param accountRepository Repository of accounts
     */
    public BalanceMoreOrEqualCheck(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Check balance
     *
     * @param accountId Account's id
     * @param amount    Value to compare
     * @return Error message
     */
    public String check(@NonNull Long accountId, @NonNull BigDecimal amount) {
        Account account = accountRepository.getById(accountId);
        if (account == null) {
            return String.format("Account with id=%s not found.", accountId);
        } else if (account.getBalance().compareTo(amount) < 0) {
            return String.format("Account's balanse %s is less then amount %s", account.getBalance(), amount);
        } else {
            return "";
        }
    }
}
