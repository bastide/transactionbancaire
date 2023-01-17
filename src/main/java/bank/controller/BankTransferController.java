package bank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import bank.service.BankService;
import bank.entity.Account;

@RestController
@RequestMapping(path = "bankService") // chemin d'accès
@Slf4j // Logger
public class BankTransferController {
    private final BankService bankService;

    // @Autowired
    public BankTransferController(BankService service) {
        this.bankService = service;
    }

    /**
     * @return la liste des comptes dans le corps de la réponse (JSON)
     */
    @GetMapping(path = "allAccounts")
    public List<Account> getAccounts() {
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
    public List<Account> transferMoney(
        @RequestParam(required = true) int fromAccount, 
        @RequestParam(required = true) int toAccount,  
        @RequestParam(required = true) int amount) {

        log.debug("Appel du service de transfert bancaire");
        bankService.transferMoney(fromAccount, toAccount, amount);

        return bankService.allAccounts();
    }
}
