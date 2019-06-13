package paysys.service.account;

import lombok.NonNull;
import paysys.domain.Account;
import paysys.repository.AccountRepository;
import paysys.utils.AppException;

import java.math.BigDecimal;

/**
 * Service for work with account
 */
public class AccountServiceImpl implements AccountService {

    /**
     * Accounts repository
     */
    private AccountRepository accountRepository;

    /**
     * Constructor with account repository
     *
     * @param accountRepository Account repository
     */
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Get account by Id from repository
     *
     * @param id Account ID
     * @return Account
     */
    @Override
    public Account getById(@NonNull Long id) {
        return accountRepository.getById(id);
    }

    /**
     * Create account by email
     *
     * @param email Account email
     * @return Created accounted from repository
     */
    @Override
    public Account create(String email) {
        Account acc = new Account(email);
        return accountRepository.save(acc);
    }

    /**
     * Increase account balance
     *
     * @param id     Account Id
     * @param amount Amount by which the balance will increase
     * @throws AppException if Account was not found
     */
    @Override
    public void increaseAccountBalance(@NonNull Long id, @NonNull BigDecimal amount) {
        accountRepository.lock(id);
        try {
            Account account = accountRepository.getById(id);
            if (account == null) {
                throw new AppException(String.format("Счет (id=%s) не существует", id));
            }
            account.setBalance(account.getBalance().add(amount));
            accountRepository.update(account);
        } finally {
            accountRepository.unlock(id);
        }
    }

    /**
     * Decrease account balance
     *
     * @param id     Account Id
     * @param amount Amount by which the balance will decrease
     * @throws AppException if Account was not found
     *                      * @throws AppException if balance is smaller then amount
     */
    public void decreaseAccountBalance(@NonNull Long id, @NonNull BigDecimal amount) {
        accountRepository.lock(id);
        try {
            Account account = accountRepository.getById(id);
            if (account == null) {
                throw new AppException(String.format("Счет (id=%s) не существует", id));
            }
            if (account.getBalance().compareTo(amount) < 0) {
                throw new AppException(String.format("Баланс %s счета(id=%s) меньше снимаемой суммы %s", account.getBalance(),
                        id, amount));
            }

            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.update(account);
        } finally {
            accountRepository.unlock(id);
        }
    }
}
