package paysys.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

/**
 *
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Account {
    /**
     * Identifier
     */
    private Long id;
    /**
     * Account's balance
     */
    private BigDecimal balance;
    /**
     * Account's email
     */
    private String email;

    /**
     * Constructor for Account
     *
     * @param email Account's email
     */
    public Account(String email) {
        this.email = email;
        this.balance = BigDecimal.ZERO;
    }

    /**
     * Set balance value
     *
     * @param balance new value
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * Set email value
     *
     * @param email new value
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
