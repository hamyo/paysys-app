package paysys.check;

import lombok.NonNull;
import paysys.domain.Account;
import paysys.repository.AccountRepository;

/**
 * Account exist check
 */
public class AccountExistsCheck {
    /**
     * Repository of accounts
     */
    private AccountRepository accountRepository;

    /**
     * Constructor fo check
     *
     * @param accountRepository Repository of accounts
     */
    public AccountExistsCheck(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Check for account's existing
     *
     * @param accountId Account's id
     * @return Error message
     */
    public String check(@NonNull Long accountId) {
        Account account = accountRepository.getById(accountId);
        if (account == null) {
            return String.format("Нет счета с id=%s.", accountId);
        } else {
            return "";
        }
    }
}
