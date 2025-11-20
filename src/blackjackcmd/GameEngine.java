package blackjackcmd;

import java.util.Scanner;

public class GameEngine {
	private final Deck deck = new Deck();
	private final Hand playerHand = new Hand();
	private final Hand dealerHand = new Hand();
	
	private final Scanner scanner = new Scanner(System.in);
	
	public void start() {
		System.out.println("Blackjack");
		
		while(true) {
			playRound();
			System.out.println("Play again? (y/n)");
			String choice = scanner.nextLine().trim().toLowerCase();
			if(!choice.equals("y")) {
				System.out.println("Thanks for playing");
				break;
			}
		}
	}
	
	private void playRound() {
		deck.generateNewDeck();
		playerHand.clear();
		dealerHand.clear();
		
		playerHand.addCard(deck.draw());
		playerHand.addCard(deck.draw());
		dealerHand.addCard(deck.draw());
		dealerHand.addCard(deck.draw());
		
		System.out.println("Dealer shows: " + dealerHand.getCards().get(0));
		System.out.println("Your hand: " + playerHand);
		
		if(playerHand.hasBlackjack()) {
			System.out.println("Blackjack! You won");
			return;
		}
		
		playerTurn();
		if(playerHand.isBust()) {
			System.out.println("You busted. Dealer wins.");
			return;
		}
		
		dealerTurn();
		showResults();
	}
	
	private void playerTurn() {
		while(true) {
			System.out.println("\n(H)it or (S)tand");
			String choice = scanner.nextLine().trim().toLowerCase();
			
			if(choice.startsWith("h")) {
				playerHand.addCard(deck.draw());
				System.out.println("You draw: " + playerHand.getCards().get(playerHand.getCards().size() - 1));
				System.out.println("Your hand: " + playerHand);
				
				if(playerHand.isBust()) return;
			} else if(choice.startsWith("s")) {
				return;
			} else {
				System.out.println("Invalid option");
			}
		}
	}
	
	private void dealerTurn() {
		System.out.println("\n Dealers turn: ");
		System.out.println("Dealer hand: " + dealerHand);
		
		while(dealerHand.getValue() < 17) {
			Card drawn = deck.draw();
			dealerHand.addCard(drawn);
			System.out.println("Dealer draws: " + drawn + " | New hand: " + dealerHand);
		}
	}
	
	private void showResults() {
		int playerValue = playerHand.getValue();
		int dealerValue = dealerHand.getValue();
		
		System.out.println("\n=== Final Hands ====");
		System.out.println("Dealer: " + dealerHand);
		System.out.println("Player: " + playerHand);
		
		if(dealerHand.isBust()) {
			System.out.println("\nDealer busts! You win!");
		} else if (playerValue > dealerValue) {
			System.out.println("\nYou win!");
		} else if (playerValue < dealerValue) {
			System.out.println("\nDealer wins.");
		} else {
			System.out.println("\nPush (tie)");
		}
	}
	
	
	
	
}
