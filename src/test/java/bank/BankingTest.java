package bank;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.extern.log4j.Log4j2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import bank.dao.AccountRepository;
import bank.entity.Account;
import bank.service.BankTransferService;

@Log4j2 // @Log4j2 est un annotation qui permet d'ajouter un logger à la classe
// On n'utilise pas @DataJpaTest car cette annotation crée une transaction avant chaque test
// Les modififactions seraient validées (commit) ou annulées (rollback) après chaque test
@SpringBootTest // Ne crée pas de transaction, elles seront créés par le service
public class BankingTest {

	@Autowired
	AccountRepository dao;
	
	@Autowired
	BankTransferService service;
	
	@Test 
	public void unTransfertReussitAvecUnMontantSuffisant() {
		// Given: les données définies dans 'data.sql'
		int amount = 10;
		int debtorID = 0;
		int creditorID = 1;
		Account debtor = dao.findById(debtorID).orElseThrow();
		Account creditor   = dao.findById(creditorID).orElseThrow();
		int debtorTotalBefore = debtor.getTotal();
		int creditorTotalBefore = creditor.getTotal();
		// When: on exécute un transfert bancaire autorisé
		log.debug("Before Successful Transfer");
		service.transferMoney(debtorID, creditorID, amount);
		log.debug("After Successful Transfer");
		// On rafraîchit les entités pour avoir les dernières mises à jour
		// réalisées par la transaction de transfert
		debtor   = dao.findById(debtorID).orElseThrow();
		creditor = dao.findById(creditorID).orElseThrow();
		// Then: Les balances doivent avoir été mises à jour dans les 2 comptes
		assertEquals(debtorTotalBefore - amount, debtor.getTotal(),
				"Balance of debtor account should be decreased by the amount of the transfer");
		assertEquals(creditorTotalBefore + amount, creditor.getTotal(),
				"Balance of creditor account should be increased by the amount of the transfer");	
	}
	
	@Test 
	public void unTransfertEchoueAvecUnMontantInsuffisant() {
		// Given: les données définies dans 'data.sql'		
		int debtorID = 0;
		int creditorID = 1;
		Account debtor = dao.findById(debtorID).orElseThrow();
		Account creditor   = dao.findById(creditorID).orElseThrow();
		int debtorTotalBefore = debtor.getTotal();
		int creditorTotalBefore = creditor.getTotal();
		// When: On essaie de trop débiter
		log.debug("Before overdrawn Transfer");
		try {
			service.transferMoney(debtorID, creditorID, 1 + debtorTotalBefore);
			fail("On doit avoir une exception");
		} catch (DataIntegrityViolationException e) {
			// On doit passer par ici
			log.info(e.getMessage());
		}
		log.debug("After overdrawn Transfer");
		// On rafraîchit les entités pour avoir les dernières mises à jour
		// réalisées par la transaction de transfert
		// Rien ne doit avoir changé
		debtor   = dao.findById(debtorID).orElseThrow();
		creditor = dao.findById(creditorID).orElseThrow();
		assertEquals(debtorTotalBefore, debtor.getTotal(),
				"Balance of debtor account should not have changed");
		assertEquals(creditorTotalBefore, creditor.getTotal(),
				"Balance of creditor account should not have changed");		
	}


	@Test 
	public void unTransfertEchoueAvecUnCrediteurInconnu() {
		// Given: les données définies dans 'data.sql'				
		int debtorID = 0;
		int unknownCreditorID = 99;
		Account debtor = dao.findById(debtorID).orElseThrow();
		int debtorTotalBefore = debtor.getTotal();
		// When: On essaie de faire un transfert vers un compte inconnu
		log.debug("On essaie de créditer un compte inconnu");
		try {
			service.transferMoney(debtorID, unknownCreditorID, 1 );
			fail("On doit avoir une exception");
		} catch (NoSuchElementException e) {
			// On doit passer par ici			
			log.info(e.getMessage());
		}
		// On rafraîchit les entités pour avoir les dernières mises à jour
		// réalisées par la transaction de transfert		
		debtor   = dao.findById(debtorID).orElseThrow();
		// Then: Rien ne doit avoir changé
		assertEquals(debtorTotalBefore, debtor.getTotal(),
				"Balance of debtor account should not have changed");
	}

	@Test 
	public void unTransfertEchoueAvecUnDebiteurInconnu() {
		// Given: les données définies dans 'data.sql'
		int unknownDebtorID = 99;
		int creditorID = 1;
		Account creditor   = dao.findById(creditorID).orElseThrow();
		int creditorTotalBefore = creditor.getTotal();
		// When: On essaie de faire un transfert depuis un compte inconnu
		log.debug("On essaie de débiter un compte inconnu");
		try {
			service.transferMoney(unknownDebtorID, creditorID, 1 );
			fail("On doit avoir une exception");
		} catch (NoSuchElementException e) {
			// On doit passer par ici
			log.debug(e.getMessage());
		}
		// On rafraîchit les entités pour avoir les dernières mises à jour
		// réalisées par la transaction de transfert
		creditor = dao.findById(creditorID).orElseThrow();
		// Then: Rien ne doit avoir changé				
		assertEquals(creditorTotalBefore, creditor.getTotal(),
				"Balance of creditor account should not have changed");
	}
	
}
