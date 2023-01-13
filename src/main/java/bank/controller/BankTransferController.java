package bank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

import bank.service.BankService;
import bank.service.BankTransferException;
import bank.entity.Account;

@Controller
@RequestMapping(path = "bankService") // chemin d'accès
@Slf4j // Logger
public class BankTransferController {
    private final BankService bankService;

    @Autowired
    public BankTransferController(BankService service) {
        this.bankService = service;
    }

    /**
     * @return la liste des comptes dans le corps de la réponse (JSON)
     */
    @GetMapping(path = "allAccounts")
    public @ResponseBody List<Account> getAccounts() {
        log.debug("On renvoie la liste des comptes bancaires");        
        return bankService.allAccounts();
    }

    /**
     * Transfère un montant d'un compte à un autre.
     * Les paramètres sont passés par la méthode POST 
     * en format application/x-www-form-urlencoded
     * @param fromAccount l'identifiant du compte débiteur
     * @param toAccount l'identifiant du compte créditeur
     * @param amount le montant à transférer
     * @return la liste des comptes dans le corps de la réponse (JSON)
     */
    @PostMapping(path = "transferMoney")
    public @ResponseBody List<Account> transferMoney(
        @RequestParam(required = true) int fromAccount, 
        @RequestParam(required = true) int toAccount,  
        @RequestParam(required = true) int amount) {

        log.debug("Appel du service de transfert bancaire");
        bankService.transferMoney(fromAccount, toAccount, amount);

        return bankService.allAccounts();
    }

    /**
     * Traduit les exceptions de la couche "service" en réponses
     * transmises au client HTTP
     * @param e exception à traiter
     * @return les infos à transmettre au client dans le corps de la réponse en cas d'exception
     */
    @ExceptionHandler(BankTransferException.class) // Quelle exception on traite
    @ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST) // Quel code de réponse HTTP on renvoie au client
    public @ResponseBody Map<String, String> handleBankTransferException(BankTransferException e) {
        log.error("Exception levée par le service : {}", e.getMessage());
        return Map.of(
            "message", e.getMessage(), 
            "fromAccount", Integer.toString(e.getDebtorId()), 
            "toAccount", Integer.toString(e.getCreditorId()), 
            "dataLayerMessage", e.getDataLayerMessage()
        );
    }
}
