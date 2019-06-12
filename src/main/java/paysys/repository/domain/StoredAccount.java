package paysys.repository.domain;

import lombok.Getter;
import lombok.NonNull;
import paysys.domain.Account;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;


/**
 * The {@code StoredAccount} class provides model for a storing {@code Account} in repository
 **/
@Getter
public class StoredAccount extends Account {
    /**
     * Locker for safe operations
     */
    ReentrantLock locker = new ReentrantLock();

    /**
     * Constructor for {@code StoredAccount}
     *
     * @param id      Account's id
     * @param balance Account's balance
     * @param email   Account's email
     */
    private StoredAccount(Long id, BigDecimal balance, String email) {
        super(id, balance, email);
    }

    /**
     * Create a copy
     *
     * @return new {@code Account}
     */
    public Account copyToAccount() {
        return new Account(this.getId(), this.getBalance(), this.getEmail());
    }

    /**
     * Returns new {@code StoredAccount} object by {@code Account} .
     *
     * @param account Account
     * @return new StoredAccount
     */
    public static StoredAccount of(@NonNull Account account) {
        return new StoredAccount(account.getId(), account.getBalance(), account.getEmail());
    }
}
