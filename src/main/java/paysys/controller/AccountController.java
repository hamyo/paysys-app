package paysys.controller;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import paysys.domain.Account;
import paysys.domain.Operation;
import paysys.service.account.AccountService;
import paysys.utils.ErrorResponseInfo;
import paysys.utils.ExceptionUtils;
import paysys.utils.PropertiesUtils;
import scala.concurrent.Await;
import scala.concurrent.Future;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Duration;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Account's controller
 */
@Slf4j
@Path("accounts")
public class AccountController {

    /**
     * HttpServer configuration
     */
    @Context
    private Configuration configuration;

    /**
     * Account's service
     */
    @Inject
    private AccountService accountService;

    /**
     * Actor's System
     */
    @Inject
    private ActorSystem actorSystem;

    /**
     * Constructor for controller
     *
     * @param accountService Account's service
     * @param actorSystem    Actor's System
     * @param configuration  HttpServer configuration
     */
    AccountController(AccountService accountService, ActorSystem actorSystem, Configuration configuration) {
        this.accountService = accountService;
        this.actorSystem = actorSystem;
        this.configuration = configuration;
    }


    /**
     * Constructor for controller with no args
     */
    public AccountController() {
    }

    /**
     * Creates account
     *
     * @param email Account's email. Required
     * @return Response with created account if success, response with error description if failed
     */
    @POST
    @Produces(APPLICATION_JSON)
    public Response create(@FormParam("email") String email) {
        log.info("create email={} started", email);
        try {
            if (StringUtils.isBlank(email)) {
                return Response.status(Response.Status.BAD_GATEWAY)
                        .entity(ErrorResponseInfo.of("email is required"))
                        .build();
            }
            Account acc = accountService.create(email);
            log.info("create email={} finished successfully", email);
            return Response.ok(acc).build();
        } catch (Exception e) {
            log.error(String.format("create email=%s finished with error.", email), e);
            return Response.serverError().entity(ExceptionUtils.getErrorMessageForConsumers(e)).build();
        }
    }

    /**
     * Gets account by Id
     *
     * @param id Account's id. Required
     * @return Response with account if success, response with error description if failed
     */
    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Response get(@PathParam("id") Long id) {
        log.debug("get id={} called.", id);
        try {
            Account acc = accountService.getById(id);
            if (acc == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ErrorResponseInfo.of(String.format("Account with id=%s was not found", id)))
                        .build();
            } else {
                return Response.ok(acc).build();
            }
        } catch (Exception e) {
            log.error(String.format("get id=%s finished with error.", id), e);
            return Response.serverError().entity(ExceptionUtils.getErrorMessageForConsumers(e)).build();
        }
    }

    /**
     * Check of transfer operation parameters
     *
     * @param sum        Operation's sum
     * @param receiverId Receiver's id
     * @return Error's message
     */
    private String checkTransferParams(BigDecimal sum, Long receiverId) {
        String delimiter = "";
        String result = checkSum(sum);

        if (receiverId == null) {
            if (StringUtils.isNotEmpty(result)) {
                delimiter = ", ";
            }
            result += delimiter + "receiverId is required";
        }
        return result;
    }

    /**
     * Check operation's sum
     *
     * @param sum Operation's sum
     * @return Error's message
     */
    private String checkSum(BigDecimal sum) {
        if (sum == null) {
            return "sum is required";
        } else if (BigDecimal.ZERO.compareTo(sum) >= 0) {
            return "sum must be a positive";
        }
        return "";
    }

    /**
     * Creates transfer operation
     *
     * @param id         Account's id. Required
     * @param sum        Operation's sum. Required. Must be a positive
     * @param receiverId Receiver account id. Required
     * @return Response with operation if success, response with error description if failed
     */
    @POST
    @Path("{id}/operations/transfer")
    @Produces(APPLICATION_JSON)
    public Response transfer(@PathParam("id") Long id, @FormParam("sum") BigDecimal sum,
                             @FormParam("receiverId") Long receiverId) {
        log.info("transfer(id={},sum={}) started", id, sum);
        try {
            String checkRes = checkTransferParams(sum, receiverId);
            if (StringUtils.isNotEmpty(checkRes)) {
                return Response.status(Response.Status.BAD_GATEWAY)
                        .entity(ErrorResponseInfo.of(checkRes))
                        .build();
            }

            ActorSelection selection = actorSystem.actorSelection("akka://paysys/user/createTransfer");
            Timeout timeout = Timeout.create(Duration.ofSeconds(getOperationTimeout()));
            Future<Object> future = Patterns.ask(selection,
                    Operation.ofNewTransfer(id, sum, id, receiverId, null), timeout);
            Operation result = (Operation) Await.result(future, timeout.duration());
            log.info("transfer(id={},sum={}) finished successfully", id, sum);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error(String.format("transfer(id=%s,sum=%s) finished with error.", id, sum), e);
            return Response.serverError().entity(ExceptionUtils.getErrorMessageForConsumers(e)).build();
        }
    }

    /**
     * Creates operation for money adding
     *
     * @param id  Account's id. Required
     * @param sum Operation's sum. Required. Must be a positive
     * @return Response with operation if success, response with error description if failed
     */
    @POST
    @Path("{id}/operations/addmoney")
    @Produces(APPLICATION_JSON)
    public Response addMoney(@PathParam("id") Long id, @FormParam("sum") BigDecimal sum) {
        log.info("addmoney(id={},sum={}) started", id, sum);
        try {
            String checkRes = checkSum(sum);
            if (StringUtils.isNotEmpty(checkRes)) {
                return Response.status(Response.Status.BAD_GATEWAY)
                        .entity(ErrorResponseInfo.of(checkRes))
                        .build();
            }

            ActorSelection selection = actorSystem.actorSelection("akka://paysys/user/createAddMoney");
            Timeout timeout = Timeout.create(Duration.ofSeconds(getOperationTimeout()));
            Future<Object> future = Patterns.ask(selection, Operation.ofNewAddMoney(id, sum), timeout);
            Operation result = (Operation) Await.result(future, timeout.duration());
            log.info("addmoney(id={},sum={}) finished successfully", id, sum);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error(String.format("addMoney(id=%s,sum=%s) finished with error.", id, sum), e);
            return Response.serverError().entity(ExceptionUtils.getErrorMessageForConsumers(e)).build();
        }
    }

    /**
     * Gets operation's timeout
     *
     * @return Operation's timeout
     */
    private Long getOperationTimeout() {
        return (Long) configuration.getProperty(PropertiesUtils.getOperationTimeoutParamName());
    }

}
