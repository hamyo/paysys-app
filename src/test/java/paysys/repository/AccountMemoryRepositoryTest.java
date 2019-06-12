package paysys.repository;

import org.junit.Assert;
import org.junit.Test;
import paysys.domain.Account;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountMemoryRepositoryTest {

    @Test
    public void save() {
        AccountMemoryRepository repository = new AccountMemoryRepository();
        Account expected = new Account(1L, BigDecimal.ZERO, "address@gmail.com");
        Account actual = new Account("address@gmail.com");
        actual = repository.save(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getById() {
        AccountMemoryRepository repository = new AccountMemoryRepository();
        Account expected = new Account("address@gmail.com");
        expected = repository.save(expected);
        Assert.assertEquals(expected, repository.getById(expected.getId()));
    }

    @Test
    public void update() {
        AccountMemoryRepository repository = new AccountMemoryRepository();
        Account expected = new Account("address@gmail.com");
        expected = repository.save(expected);
        expected.setBalance(BigDecimal.TEN);
        repository.update(expected);
        Account actual = repository.getById(expected.getId());
        Assert.assertEquals(expected, actual);
    }
}