package blackjackcmd;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
	private final ArrayList<Card> cards = new ArrayList<>();
	
	public Deck() {
		generateNewDeck();
		shuffle();
	}
	
	public void generateNewDeck() {
		cards.clear();
		
		for(Card.Suit suit : Card.Suit.values()) {
			for(Card.Rank rank : Card.Rank.values()) {
				cards.add(new Card(rank, suit));
			}
		}
		shuffle();
	}
	
	public void shuffle() {
		Collections.shuffle(cards);
	}
	
	public Card draw() {
		return cards.remove(cards.size() - 1);
	}
	
	
}
