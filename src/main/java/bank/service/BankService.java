package bank.service;

import java.util.List;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bank.dao.AccountRepository;
import bank.entity.Account;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@Service
@Validated
public class BankService {

	private final AccountRepository dao;
	// @Autowired // Injection de dépendance par le constructeur
	public BankService(AccountRepository dao) {
		this.dao = dao;
	}

	@Transactional(readOnly = true)
	public List<Account> allAccounts() {
		return dao.findAll();
	}
	
	/**
	 * Transfère un montant d'un compte à un autre.
	 * @param fromId l'identifiant du compte débiteur
	 * @param toId L'identifiant du compte créditeur
	 * @param amount Le montant à transférer
	 * @throws BankTransferException si le transfert échoue
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void transferMoney(int fromId, int toId, @Positive int amount) {
		log.info("Début de la transaction de transfert bancaire");
		if (fromId == toId) {
			throw new BankTransferException(fromId, toId, "Impossible de transférer à l'intérieur d'un même compte", "");
		}
		// On vérifie l'existence des comptes
		Account from = dao.findById(fromId).orElseThrow(
				() -> new BankTransferException(fromId, toId, "Compte débiteur inexistant", ""));
		Account to = dao.findById(toId).orElseThrow(
				() -> new BankTransferException(fromId, toId, "Compte créditeur inexistant", ""));
		try {
			from.setBalance(from.getBalance() - amount); // Débit
			to.setBalance(to.getBalance() + amount); // Crédit
			// flush() est une méthode de l'interface CrudRepository
			// qui permet de forcer la sauvegarde des modifications dans la base de données
			// Nécessaire si on veut récupérer l'exception DataIntegrityViolationException dans la méthode
			// pour la convertir en une exception "Métier"
			// Sinon, l'exception DataIntegrityViolationException serait levée par Spring à l'extérieur de la méthode	
			dao.flush();
		} catch (ConstraintViolationException e) {
			// On récupère une exception de la couche "Accès aux données"
			String message = e.getMessage();
			log.info("Erreur de transaction: {}", message);
			// On lève une exception de la couche "Service"
			throw new BankTransferException(fromId, toId, "Crédit Insuffisant", message);
		}
		log.info("Transaction de transfert bancaire terminée avec succès");
	}
}
