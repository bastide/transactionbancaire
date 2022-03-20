package bank.service;

import bank.dao.AccountRepository;
import bank.entity.Account;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class BankTransferService {

	private final AccountRepository dao;

	public BankTransferService(AccountRepository dao) {
		this.dao = dao;
	}

	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void transferMoney(int fromId, int toId, int amount) {
		log.debug("Début du transfert bancaire");
		// si pas trouvé, lève NoSuchElementException
		Account from = dao.findById(fromId).orElseThrow();
		Account to = dao.findById(toId).orElseThrow();
		from.debit(amount);
		to.credit(amount);
		log.debug("Fin du transfert bancaire");
	}
	
}
