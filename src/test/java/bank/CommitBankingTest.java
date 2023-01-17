package bank;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bank.dao.AccountRepository;
import bank.entity.Account;
import bank.service.BankService;
import lombok.extern.log4j.Log4j2;

@Log4j2 // @Log4j2 est une annotation qui permet d'ajouter un logger à la classe
// On n'utilise pas @DataJpaTest, qui fait un "rollback" systématique après chaque test
@SpringBootTest 
class CommitBankingTest {
	static final int ID_DU_DEBITEUR = 0;
	static final int ID_DU_CREDITEUR = 1;
	static final int ID_INCONNU = 99;
	static int MONTANT_A_TRANSFERER = 10;

	Account compteDebiteur;
	Account compteCrediteur;

	int balanceInitialeDebiteur;
	int balanceInitialeCrediteur;

	@Autowired 
	BankService bankService;


	@Autowired 
	AccountRepository dao;

	@BeforeEach() 
	void initialiserLesDonneesDeTest() {
		log.info("Initialisations avant la transaction");
		compteDebiteur = dao.findById(ID_DU_DEBITEUR).orElseThrow();
		compteCrediteur   = dao.findById(ID_DU_CREDITEUR).orElseThrow();
		balanceInitialeDebiteur = compteDebiteur.getBalance();
		balanceInitialeCrediteur = compteCrediteur.getBalance();
	}	
	
	@Test
	void unTransfertBancaireReussit() {
		log.info("Début d'un transfert autorisé");
		// Given: les données définies dans 'data.sql'
		// When: on exécute un transfert bancaire autorisé
		bankService.transferMoney(ID_DU_DEBITEUR, ID_DU_CREDITEUR, MONTANT_A_TRANSFERER);
		log.info("Fin d'un transfert autorisé");
	}

	
	@AfterEach() 
	void verifieTransfertOk() {
		log.debug("Vérifications après la transaction");		
		// On rafraîchit les entités pour avoir les dernières mises à jour
		// réalisées par la transaction de transfert
		compteDebiteur   = dao.findById(ID_DU_DEBITEUR).orElseThrow();
		compteCrediteur = dao.findById(ID_DU_CREDITEUR).orElseThrow();
		// Then: Les balances doivent avoir été mises à jour dans les 2 comptes
		assertEquals(balanceInitialeDebiteur - MONTANT_A_TRANSFERER, compteDebiteur.getBalance(),
				"Balance of debtor account should be decreased by the amount of the transfer");
		assertEquals(balanceInitialeCrediteur + MONTANT_A_TRANSFERER, compteCrediteur.getBalance(),
				"Balance of creditor account should be increased by the amount of the transfer");	
	}
}
