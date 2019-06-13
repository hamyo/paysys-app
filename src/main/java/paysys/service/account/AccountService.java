package paysys.service.account;

import lombok.NonNull;
import paysys.domain.Account;

import java.math.BigDecimal;

/**
 * Implementing this interface allows an object to work with {@code Account}
 */
public interface AccountService {

    /**
     * Get account by Id
     *
     * @param id Account ID
     * @return Account
     */
    Account getById(@NonNull Long id);

    /**
     * Create account by email
     *
     * @param email Account email
     * @return Created accounted
     */
    Account create(String email);

    /**
     * Increase account balance
     *
     * @param id     Account Id
     * @param amount Amount by which the balance will increase
     */
    void increaseAccountBalance(Long id, BigDecimal amount);

    /**
     * Decrease account balance
     *
     * @param id     Account Id
     * @param amount Amount by which the balance will decrease
     */
    void decreaseAccountBalance(Long id, BigDecimal amount);
}
