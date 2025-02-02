package bank.service;

import java.util.List;

import bank.dao.JournalRepository;
import bank.entity.Journal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import bank.dao.AccountRepository;
import bank.entity.Account;
import jakarta.validation.constraints.Positive;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Validated // Les annotations de validation sont actives sur les méthodes de ce service
			// (ex: @Positive)
public class BankService {

	private final AccountRepository accountDao;
    private final JournalRepository journalDao;

	// @Autowired // Injection de dépendance par le constructeur
	public BankService(AccountRepository accountDao, JournalRepository journalDao) {
		this.accountDao = accountDao;
        this.journalDao = journalDao;
    }

	@Transactional(readOnly = true)
	public List<Account> allAccounts() {
		return accountDao.findAll();
	}

	/**
	 * Transfère un montant d'un compte à un autre.
	 *
	 * @param fromId l'identifiant du compte débiteur
	 * @param toId   L'identifiant du compte créditeur
	 * @param amount Le montant à transférer (positif)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void transferMoney(int fromId, int toId,
			@Positive(message = "Le montant d'un transfert doit être positif") int amount) {
		log.info("Début du service de transfert bancaire");
		if (fromId == toId) {
			throw new IllegalArgumentException("Les deux comptes doivent être différents");
		}
		// On vérifie l'existence des comptes
		Account from = accountDao.findById(fromId).orElseThrow();
		Account to = accountDao.findById(toId).orElseThrow();
        // On enregistre le transfert dans le journal
        journalDao.save(new Journal(amount, from, to));
        // On effectue le transfert
		from.setBalance(from.getBalance() - amount); // Débit
		to.setBalance(to.getBalance() + amount); // Crédit
		// Inutile de faire dao.save(from) et dao.save(to)
		// car les entités modifiées dans une transaction sont automatiquement sauvegardées
        log.info("Fin du service de transfert bancaire");
	}

}
