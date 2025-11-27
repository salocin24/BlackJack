package blackjackcmd;


public class GameEngine {
	
	public enum GameState {
		NOT_STARTED,
		PLAYER_TURN,
		DEALER_TURN,
		ROUND_OVER
	}
	
	public enum Outcome {
		NONE,
		PLAYER_WIN,
		DEALER_WIN,
		PUSH
	}
	
	private final Deck deck = new Deck();
	private final Hand playerHand = new Hand();
	private final Hand dealerHand = new Hand();
	
	private GameState state = GameState.NOT_STARTED;
	private Outcome outcome = Outcome.NONE;
	private boolean hideDealerHoleCard = true;
	
	public void start() {
		deck.generateNewDeck();
		playerHand.clear();
		dealerHand.clear();
		outcome = Outcome.NONE;
		
		playerHand.addCard(deck.draw());
		playerHand.addCard(deck.draw());
		dealerHand.addCard(deck.draw());
		dealerHand.addCard(deck.draw());
		
		hideDealerHoleCard = true;
		
		if (playerHand.hasBlackjack()) {
			hideDealerHoleCard = false;
			state = GameState.ROUND_OVER;
			showResults();
		} else {
			state = GameState.PLAYER_TURN;
		}
	}
	
	public void playerHit() {
		if (state != GameState.PLAYER_TURN) return;
		
		playerHand.addCard(deck.draw());
		if (playerHand.isBust()) {
			state = GameState.ROUND_OVER;
			showResults();
		}
	}
	
	public void playerStand() {
		if (state != GameState.PLAYER_TURN) return;
		
		hideDealerHoleCard = true;
		
		state = GameState.DEALER_TURN;
		dealerPlay();
		state = GameState.ROUND_OVER;
		showResults();
	}
	
	private void dealerPlay() {
		while (dealerHand.getValue() < 17) {
			dealerHand.addCard(deck.draw());
		}
	}
	
	
	
	private void showResults() {
		hideDealerHoleCard = false;
		int playerValue = playerHand.getValue();
		int dealerValue = dealerHand.getValue();
		
		boolean playerBust = playerHand.isBust();
		boolean dealerBust = dealerHand.isBust();
		
		
		if(playerBust && dealerBust) {
			outcome = Outcome.PUSH; // both bust, tie
		} else if (playerBust) {
			outcome = Outcome.DEALER_WIN;
		} else if (dealerBust) {
			outcome = Outcome.PLAYER_WIN;
		} else if (playerValue > dealerValue) {
			outcome = Outcome.PLAYER_WIN;
		} else if (playerValue < dealerValue) {
			outcome = Outcome.DEALER_WIN;
		} else {
			outcome = Outcome.PUSH;
		}
	}
	public boolean isDealerHoleCardHidden() {
		return hideDealerHoleCard;
	}
	
	public Hand getPlayerHand() {
		return playerHand;
	}
	public Hand getDealerHand() {
		return dealerHand;
	}
	public GameState getGameState() {
		return state;
	}
	public Outcome getOutcome() {
		return outcome;
	}

	
}
