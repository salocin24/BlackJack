package blackjackcmd;

import java.util.ArrayList;
import java.util.List;

public class Hand {
	private final ArrayList<Card> cards = new ArrayList<>();
	
	public void addCard(Card card) {
		cards.add(card);
	}
	
	public void clear() {
		cards.clear();
	}
	
	public List<Card> getCards() {
		return List.copyOf(cards);
	}
	
	public int getValue() {
		int value = 0;
		int aces = 0;
		
		for(Card card : cards) {
			switch(card.getRank()) {
			case JACK:
			case QUEEN:
			case KING:
				value += 10;
				break;
			case ACE:
				aces++;
				break;
			default:
				value += card.getRank().ordinal() + 2;
				break;
			}
		}
		
		while(aces > 0) {
			if (value + 11 <= 21) {
				value += 11;
			} else {
				value += 1;
			}
			aces--;
		}
		
		return value;
	}
	
	public boolean isBust() {
		return getValue() > 21;
	}
	
	public boolean hasBlackjack() {
		return cards.size() == 2 && getValue() == 21;
	}
	
	@Override
	public String toString() {
		return cards.toString() + " Value: " + getValue();
	}
}
