package ticketmachine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketMachineTest {

    private static final int PRICE = 50; // Une constante
    private static final int NEGATIVE_PRICE = -10;
    private static final int NEGATIVE_AMOUNT = -5;

    private TicketMachine machine; // l'objet à tester

    @BeforeEach
    public void setUp() {
        machine = new TicketMachine(PRICE); // On initialise l'objet à tester
    }

    @Test
    // S1 : le prix affiché correspond à l'initialisation
    void priceIsCorrectlyInitialized() {
        assertEquals(PRICE, machine.getPrice(), "Initialisation incorrecte du prix");
    }

    @Test
    // S2 : la balance change quand on insère de l'argent
    void insertMoneyChangesBalance() {
        machine.insertMoney(10);
        machine.insertMoney(20);
        assertEquals(10 + 20, machine.getBalance(), "La balance n'est pas correctement mise à jour");
    }

    @Test
    // S3 : on n'imprime pas le ticket si le montant inséré est insuffisant
    void noPrintWhenInsufficientAmount() {
        machine.insertMoney(PRICE - 1); // Insère 49
        assertFalse(machine.printTicket(), "Le ticket ne devrait pas être imprimé avec un montant insuffisant");
    }

    @Test
    // S4 : on imprime le ticket si le montant inséré est suffisant
    void printWhenSufficientAmount() {
        machine.insertMoney(PRICE); // Insère exactement 50
        assertTrue(machine.printTicket(), "Le ticket devrait être imprimé avec un montant suffisant");
    }

    @Test
    // S5 : Quand on imprime un ticket la balance est décrémentée du prix du ticket
    void balanceDecrementedAfterPrinting() {
        int initialAmount = PRICE + 30; // 80
        machine.insertMoney(initialAmount);
        int initialBalance = machine.getBalance(); // 80

        machine.printTicket();

        assertEquals(initialBalance - PRICE, machine.getBalance(),
                "La balance devrait être décrémentée du prix après impression");
    }

    @Test
    // S6 : le montant collecté est mis à jour quand on imprime un ticket (pas avant)
    void totalCollectUpdatedOnlyWhenPrinting() {
        int initialTotal = machine.getTotal();

        // Insérer de l'argent ne change pas le total collecté
        machine.insertMoney(PRICE);
        int totalAfterInsert = machine.getTotal();
        assertEquals(initialTotal, totalAfterInsert,
                "Le total collecté ne devrait pas changer avant l'impression");

        // Imprimer le ticket met à jour le total collecté
        machine.printTicket();
        int totalAfterPrint = machine.getTotal();
        assertEquals(initialTotal + PRICE, totalAfterPrint,
                "Le total collecté devrait être mis à jour après l'impression");
    }

    @Test
    // S7 : refund() rend correctement la monnaie
    void refundReturnsCorrectAmount() {
        int insertedAmount = 80;
        machine.insertMoney(insertedAmount);

        int refund = machine.refund();

        assertEquals(insertedAmount, refund,
                "Le remboursement devrait retourner le montant exact inséré");
    }

    @Test
    // S8 : refund() remet la balance à zéro
    void refundResetsBalance() {
        machine.insertMoney(100);
        machine.refund();
        assertEquals(0, machine.getBalance(), "La balance n'a pas été réinitialisée après remboursement");
    }

    @Test
    // S9 : on ne peut pas insérer un montant négatif
    void cannotInsertNegativeAmount() {
        // Vérifie qu'une exception est levée quand on insère un montant négatif
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            machine.insertMoney(NEGATIVE_AMOUNT);
        }, "insertMoney() devrait refuser les montants négatifs");
        
        assertTrue(exception.getMessage().contains("positif"));
    }

    @Test
    // S10 : on ne peut pas créer de machine qui délivre des tickets dont le prix est négatif
    void cannotCreateMachineWithNegativePrice() {
        // Vérifie qu'une exception est levée pour les prix négatifs
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new TicketMachine(NEGATIVE_PRICE);
        }, "Le constructeur devrait refuser un prix négatif");

        assertTrue(exception.getMessage().contains("positive"));
    }

    // Tests supplémentaires pour vérifier le comportement complet
    @Test
    void printWithExactAmount() {
        machine.insertMoney(PRICE);
        boolean result = machine.printTicket();

        assertTrue(result, "Devrait imprimer avec le montant exact");
        assertEquals(0, machine.getBalance(), "Balance devrait être à 0 après impression avec montant exact");
    }

    @Test
    void printWithMoreThanPrice() {
        machine.insertMoney(PRICE + 25);
        boolean result = machine.printTicket();

        assertTrue(result, "Devrait imprimer avec un montant supérieur");
        assertEquals(25, machine.getBalance(), "Balance devrait garder l'excédent");
    }

    @Test
    void refundWithZeroBalance() {
        int refund = machine.refund();
        assertEquals(0, refund, "Le remboursement devrait être 0 quand la balance est 0");
        assertEquals(0, machine.getBalance(), "La balance devrait rester à 0");
    }

    @Test
    void multipleTicketsWithSufficientBalance() {
        machine.insertMoney(PRICE * 3); // 150

        boolean firstPrint = machine.printTicket();
        boolean secondPrint = machine.printTicket();
        boolean thirdPrint = machine.printTicket();

        assertTrue(firstPrint, "Première impression devrait réussir");
        assertTrue(secondPrint, "Deuxième impression devrait réussir");
        assertTrue(thirdPrint, "Troisième impression devrait réussir");
        assertEquals(0, machine.getBalance(), "Balance devrait être à zéro après trois impressions");
        assertEquals(PRICE * 3, machine.getTotal(), "Total collecté devrait être 150");
    }

    @Test
    void printWithNoMoney() {
        boolean result = machine.printTicket();
        assertFalse(result, "Ne devrait pas imprimer sans argent");
        assertEquals(0, machine.getBalance(), "Balance devrait rester à 0");
        assertEquals(0, machine.getTotal(), "Total collecté devrait rester à 0");
    }
}