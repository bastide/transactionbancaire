package bank;

import bank.dao.JournalRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bank.dao.AccountRepository;
import bank.entity.Account;
import bank.service.BankService;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.TransactionSystemException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Une série de tests unitaires qui vont provoquer un "rollback"
 * dans le service bancaire
 */
@Log4j2 // @Log4j2 est une annotation qui permet d'ajouter un logger à la classe
// On n'utilise pas @DataJpaTest, qui fait un "rollback" systématique après chaque test
@SpringBootTest
class RolllbackBankingTest {
	static final int ID_DU_DEBITEUR = 0;
	static final int ID_DU_CREDITEUR = 1;
	static final int ID_INCONNU = 99;

	Account debtor;
	Account creditor;

	int debtorTotalBefore;
	int creditorTotalBefore;
    long journalEntriesBefore;

	@Autowired
	AccountRepository accountDao;
    @Autowired
    JournalRepository journalDao;
	@Autowired
	BankService service;

	@BeforeEach()
	void initialiserLesDonneesDeTest() {
		log.info("Initialisations avant la transaction");
		debtor = accountDao.findById(ID_DU_DEBITEUR).orElseThrow();
		creditor   = accountDao.findById(ID_DU_CREDITEUR).orElseThrow();
		debtorTotalBefore = debtor.getBalance();
		creditorTotalBefore = creditor.getBalance();
        journalEntriesBefore = journalDao.count();
	}

	@AfterEach()
	void verifierRollbackEffectif() {
		log.info("Vérifications après la transaction");
		// On rafraîchit les entités pour avoir les dernières mises à jour
		// réalisées par la transaction de transfert
		// Rien ne doit avoir changé
		debtor   = accountDao.findById(ID_DU_DEBITEUR).orElseThrow();
		creditor = accountDao.findById(ID_DU_CREDITEUR).orElseThrow();
		assertEquals(debtorTotalBefore, debtor.getBalance(),
				"Balance of debtor account should not have changed");
		assertEquals(creditorTotalBefore, creditor.getBalance(),
				"Balance of creditor account should not have changed");
        assertEquals(journalEntriesBefore, journalDao.count(),
                "Journal entries should not have changed");
	}

	@Test
	void lesDecouvertsSontInterdits() {
		log.info("On essaie de trop débiter");
		// Given: les données définies dans 'data.sql'
		// When: On essaie de trop débiter
		var ex = assertThrows(
				TransactionSystemException.class,
				() -> service.transferMoney(ID_DU_DEBITEUR, ID_DU_CREDITEUR, 1 + debtorTotalBefore),
				"Découverts interdits !"
		);
		log.info("Exception: {}", ex.getMessage());
	}


	@Test
	void unTransfertEchoueAvecUnCrediteurInconnu() {
		log.info("On essaie de faire un transfert vers un compte inconnu");
		// Given: les données définies dans 'data.sql'
		// When: On essaie de faire un transfert vers un compte inconnu
		assertThrows(
				NoSuchElementException.class,
				() -> service.transferMoney(ID_DU_DEBITEUR, ID_INCONNU, 1),
				"Le compte crediteur est inconnu !"
		);
	}

	@Test
	void unTransfertEchoueAvecUnDebiteurInconnu() {
		log.info("On essaie de débiter un compte inconnu");
		// Given: les données définies dans 'data.sql'
		// When: On essaie de faire un transfert depuis un compte inconnu
		assertThrows(
				NoSuchElementException.class,
				() -> service.transferMoney(ID_INCONNU, ID_DU_CREDITEUR, 1),
				"Le compte crediteur est inconnu !"
		);
	}

	@Test
	void transfertEchoueSiDebiteurEtCrediteurIdentiques() {
		log.info("On essaie un transfert avec debiteur = crediteur");
		// When: Le débiteur et le créditeur sont identiques lors d'un transfert
		assertThrows(
				IllegalArgumentException.class,
				() -> service.transferMoney(ID_DU_DEBITEUR, ID_DU_DEBITEUR, 1),
				"Les deux comptes doivent être différents !"
		);
	}

	@Test
	void leMontantDuTransfertDoitEtrePositif() {
		log.info("On essaie un transfert avec un montant négatif");
		assertThrows(ConstraintViolationException.class,
				() -> service.transferMoney(ID_DU_DEBITEUR, ID_DU_CREDITEUR, -1 ) ,
				"Le montant du transfert doit être positif"
		);
		log.info("Après avoir essayé de débiter le créditeur");
	}
}
