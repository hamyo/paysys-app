package paysys.service.operation;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import paysys.check.AccountExistsCheck;
import paysys.check.BalanceMoreOrEqualCheck;
import paysys.domain.Operation;
import paysys.utils.AppException;

/**
 * Creates new operation for transfer money from one account to another
 */
@Slf4j
public class TransferCreateActor extends AbstractActor {
    /**
     * Constructor for actor
     * @param operationService Operation service
     * @param balanceMoreOrEqualCheck Balance check
     * @param accountExistsCheck Account exist check
     * @param processActor Actor for further processing
     */
    private TransferCreateActor(OperationService operationService, BalanceMoreOrEqualCheck balanceMoreOrEqualCheck,
                                AccountExistsCheck accountExistsCheck, ActorRef processActor) {
        this.operationService = operationService;
        this.balanceMoreOrEqualCheck = balanceMoreOrEqualCheck;
        this.accountExistsCheck = accountExistsCheck;
        this.processActor = processActor;
    }

    /**
     *
     * @param operationService Operation service
     * @param balanceMoreOrEqualCheck Balance check
     * @param accountExistsCheck Account exist check
     * @param processActor Actor for further processing
     * @return ActorRef configuration object
     */
    static public Props props(OperationService operationService, BalanceMoreOrEqualCheck balanceMoreOrEqualCheck,
                              AccountExistsCheck accountExistsCheck, ActorRef processActor) {
        return Props.create(TransferCreateActor.class, () -> new TransferCreateActor(operationService,
                balanceMoreOrEqualCheck, accountExistsCheck, processActor));
    }

    /**
     * Operation service
     */
    private OperationService operationService;
    /**
     * Balance check
     */
    private BalanceMoreOrEqualCheck balanceMoreOrEqualCheck;
    /**
     * Account exist check
     */
    private AccountExistsCheck accountExistsCheck;
    /**
     * Actor for further processing
     */
    private ActorRef processActor;

    /**
     * Creates a receive
     * @return Receive
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Operation.class, op -> {
                    try {
                        Operation savedOp = handleMessage(op);
                        getSender().tell(savedOp, getSelf());
                        processActor.tell(savedOp.getId(), ActorRef.noSender());
                    } catch (Exception ex) {
                        log.error(String.format("Error processing message(%s).", op), ex);
                        getSender().tell(new akka.actor.Status.Failure(ex), getSelf());
                        throw ex;
                    }
                })
                .build();
    }

    /**
     * Message's handling
     * @param operation Handling operation
     * @return Saved operation
     */
    Operation handleMessage(Operation operation) {
        String checkResult = check(operation);
        if (StringUtils.isNotEmpty(checkResult)) {
            throw new AppException(checkResult);
        }

        return operationService.save(operation);
    }

    /**
     * Internal check
     * @param operation Handling operation
     * @return Check's error
     */
    String check(Operation operation) {
        String checkResult = accountExistsCheck.check(operation.getSenderId());
        if (StringUtils.isNotEmpty(checkResult)) {
            return checkResult;
        } else {
            checkResult = accountExistsCheck.check(operation.getReceverId());
            if (StringUtils.isNotEmpty(checkResult)) {
                return checkResult;
            } else {
                return balanceMoreOrEqualCheck.check(operation.getSenderId(), operation.getSum());
            }
        }
    }
}
