package bank.service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Exception levée lors d'un transfert bancaire.
 */
@Getter @ToString @RequiredArgsConstructor // Lombok
public class BankTransferException extends RuntimeException {
    private final int debtorId;
    private final int creditorId;
    // Un message clair pour l'utilisateur
    private final String message;
    // Un message d'erreur pour le développeur
    private final String dataLayerMessage; 
}
