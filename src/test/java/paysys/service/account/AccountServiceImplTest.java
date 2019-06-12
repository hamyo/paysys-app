package paysys.service.account;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import paysys.domain.Account;
import paysys.repository.AccountMemoryRepository;
import paysys.repository.AccountRepository;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountServiceImplTest {

    @Test
    public void getById_ID1_Exist() {
        AccountRepository accountRepository = mock(AccountRepository.class);
        when(accountRepository.getById(1L)).thenReturn(new Account(1L, BigDecimal.TEN, "address@gmail.com"));
        AccountService accountService = new AccountServiceImpl(accountRepository);
        Account expected = new Account(1L, BigDecimal.TEN, "address@gmail.com");
        Account actual = accountService.getById(expected.getId());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getById_ID2_NotExist() {
        AccountRepository accountRepository = mock(AccountRepository.class);
        when(accountRepository.getById(1L)).thenReturn(new Account(1L, BigDecimal.TEN, "address@gmail.com"));
        AccountService accountService = new AccountServiceImpl(accountRepository);
        Account actual = accountService.getById(2L);
        Assert.assertNull(actual);
    }

    @Test
    public void create() {
        AccountRepository accountRepository = mock(AccountRepository.class);
        when(accountRepository.save(new Account("address@gmail.com")))
                .thenReturn(new Account(1L, BigDecimal.ZERO, "address@gmail.com"));
        AccountService accountService = new AccountServiceImpl(accountRepository);
        Account expected = new Account(1L, BigDecimal.ZERO, "address@gmail.com");
        Account actual = accountService.create("address@gmail.com");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void increaseAccountBalance_ID1_AMOUNT100() {
        BigDecimal incAmount = BigDecimal.valueOf(100);
        AccountService accountService = new AccountServiceImpl(new AccountMemoryRepository());
        Account actual = accountService.create("address@gmail.com");
        BigDecimal startBalance = actual.getBalance();

        accountService.increaseAccountBalance(actual.getId(), incAmount);
        Account changed = accountService.getById(actual.getId());
        Assert.assertTrue(changed.getBalance().subtract(startBalance).compareTo(incAmount) == 0);
    }
}