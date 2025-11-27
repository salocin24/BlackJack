package blackjackcmd;

import java.util.List;

import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application{
	
	private GameEngine engine;
	private HBox dealerCardsBox;
	private HBox playerCardsBox;
	
	private Label dealerValueLabel;
	private Label playerValueLabel;
	private Label statusLabel;
	private Label dealerWinLabel;
	private Label playerWinLabel;
	
	private int playerWins = 0;
	private int dealerWins = 0;
	
	private Button dealButton;
	private Button hitButton;
	private Button standButton;
	private Button playAgainButton;
	
	private boolean dealerHoleWasHidden = false;
	
	private int previousPlayerCardCount = 0;
	private int previousDealerCardCount = 0;
	
	public void start(Stage primaryStage) {
		engine = new GameEngine();
		
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(15));
		
		// top bar for win representation
		HBox topBar = new HBox(20);
		topBar.setPadding(new Insets(10));
		topBar.setAlignment(Pos.CENTER_LEFT);
		
		// win labels (left side - dealer at top, player at bottom)
		dealerWinLabel = new Label("Dealer Wins: 0");
		dealerWinLabel.getStyleClass().add("win-label");
		playerWinLabel = new Label("Player Wins: 0");
		playerWinLabel.getStyleClass().add("win-label");
		
		topBar.getChildren().addAll(dealerWinLabel, playerWinLabel);
		
		// dealer area
		VBox dealerArea = new VBox(10);
		Label dealerLabel = new Label("Dealer");
		dealerCardsBox = new HBox(5);
		dealerCardsBox.setMinHeight(150); // Reserve space for cards
		dealerCardsBox.setAlignment(Pos.CENTER);
		
		dealerValueLabel = new Label("Value: 0");
		
		dealerArea.setAlignment(Pos.CENTER);
		dealerArea.setPadding(new Insets(20, 0, 20, 0));
		dealerArea.getChildren().addAll(dealerLabel, dealerCardsBox, dealerValueLabel);
		
		// combined top and dealerArea
		VBox top = new VBox(10, topBar, dealerArea);
		root.setTop(top);
		
		// player area
		VBox playerArea = new VBox(10);
		Label playerLabel = new Label("Player");
		playerCardsBox = new HBox(5);
		playerCardsBox.setMinHeight(150); // Reserve space for cards
		playerCardsBox.setAlignment(Pos.CENTER);
		playerValueLabel = new Label("Value: 0");
		playerArea.setAlignment(Pos.CENTER);
		playerArea.setPadding(new Insets(20, 0, 20, 0));
		playerArea.getChildren().addAll(playerLabel, playerCardsBox, playerValueLabel);
		
		
		
		// game status
		statusLabel = new Label("Click DEAL to start");
		VBox centerBox = new VBox(20, statusLabel);
		centerBox.setAlignment(Pos.CENTER);
		centerBox.setPadding(new Insets(20, 0, 20, 0));
		
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(10, 0, 0, 0));
		
		dealButton = new Button("Deal");
		hitButton = new Button("Hit");
		standButton = new Button("Stand");
		playAgainButton = new Button("Play Again");
		
		dealButton.getStyleClass().add("big-button");
		hitButton.getStyleClass().add("big-button");
		standButton.getStyleClass().add("big-button");
		playAgainButton.getStyleClass().add("big-button");
		
		statusLabel.getStyleClass().add("status-label");
		dealerValueLabel.getStyleClass().add("win-label");
		dealerLabel.getStyleClass().add("win-label");
		playerValueLabel.getStyleClass().add("win-label");
		playerLabel.getStyleClass().add("win-label");
		
		hitButton.setDisable(true);
		standButton.setDisable(true);
		playAgainButton.setDisable(true);
		
		dealButton.setOnAction(e -> onDeal());
		hitButton.setOnAction(e -> onHit());
		standButton.setOnAction(e -> onStand());
		playAgainButton.setOnAction(e -> onPlayAgain());
		
		buttonBox.getChildren().addAll(dealButton, hitButton, standButton, playAgainButton);
		
		VBox bottom = new VBox(10, playerArea, buttonBox);
		root.setCenter(centerBox);
		root.setBottom(bottom);
		
		Scene scene = new Scene(root, 600, 800);
		scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
		primaryStage.setTitle("Blackjack (JavaFX)");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void onDeal() {
		// Reset card counts when starting a new game
		previousPlayerCardCount = 0;
		previousDealerCardCount = 0;
		
		engine.start();
		updateView();
		
		statusLabel.setText("Your turn: Hit or Stand.");
		dealButton.setDisable(true);
		hitButton.setDisable(false);
		standButton.setDisable(false);
		playAgainButton.setDisable(true);
		
		if(engine.getGameState() == GameEngine.GameState.ROUND_OVER) {
			showOutcome();
		}
	}
	
	private void onHit() {
		engine.playerHit();
		updateView();
		
		if (engine.getGameState() == GameEngine.GameState.ROUND_OVER) {
			showOutcome();
		}
	}
	
	private void onStand() {
		engine.playerStand();
		updateView();
		showOutcome();
	}
	
	private void onPlayAgain() {
		// Reset card counts when starting a new game
		previousPlayerCardCount = 0;
		previousDealerCardCount = 0;
		
		engine.start();
		updateView();
		
		statusLabel.setText("Your turn: Hit or Stand.");
		
		dealButton.setDisable(true);
		hitButton.setDisable(false);
		standButton.setDisable(false);
		playAgainButton.setDisable(true);
	}
	
	
	private void updateView() {
		List<Card> dealerCards = engine.getDealerHand().getCards();
		List<Card> playerCards = engine.getPlayerHand().getCards();
		
		ImageView holeViewForReveal = null;
		
		boolean currentlyHidden = (engine.getGameState() == GameEngine.GameState.PLAYER_TURN);
		boolean doRevealAnimation = dealerHoleWasHidden && !currentlyHidden;
		boolean holeCardStateChanged = (dealerHoleWasHidden != currentlyHidden);
		
		// Only update dealer cards if count changed OR hole card visibility changed
		if (dealerCards.size() != previousDealerCardCount || holeCardStateChanged) {
			// Remove old cards and add all current cards
			dealerCardsBox.getChildren().clear();
			
			for(int i = 0; i < dealerCards.size(); i++) {
				Card card = dealerCards.get(i);
				ImageView view;
				
				if (i == 1 && currentlyHidden) {
					view = createBackCardImage();
					dealerCardsBox.getChildren().add(view);
					// Only animate if this is a new card (not just a state change)
					if (i >= previousDealerCardCount) {
						animateDealCard(view, true);
					}
				} else {
					view = createCardImage(card);
					dealerCardsBox.getChildren().add(view);
					
					if (i == 1 && doRevealAnimation) {
						// hidden -> revealed, flip instead of slide
						holeViewForReveal = view;
					} else if (i >= previousDealerCardCount) {
						// Only animate new cards
						animateDealCard(view, true);
					}
				}
			}
		}
		
		// Only update player cards if count changed
		if (playerCards.size() != previousPlayerCardCount) {
			// Remove old cards and add all current cards
			playerCardsBox.getChildren().clear();
			
			for (int i = 0; i < playerCards.size(); i++) {
				Card card = playerCards.get(i);
				ImageView view = createCardImage(card);
				playerCardsBox.getChildren().add(view);
				// Only animate if this is a new card
				if (i >= previousPlayerCardCount) {
					animateDealCard(view, false);
				}
			}
		}
		
		playerCardsBox.setAlignment(Pos.CENTER);
		dealerCardsBox.setAlignment(Pos.CENTER);
		
		dealerValueLabel.setText(engine.isDealerHoleCardHidden() ? "Value: ?" : "Value: " + engine.getDealerHand().getValue());
		playerValueLabel.setText("Value: " + engine.getPlayerHand().getValue());
		
		if (doRevealAnimation && holeViewForReveal != null) {
			animateReveal(holeViewForReveal);
		}
		
		// Update previous counts
		previousPlayerCardCount = playerCards.size();
		previousDealerCardCount = dealerCards.size();
		dealerHoleWasHidden = currentlyHidden;
	}
	
	private void animateDealCard(ImageView view, boolean fromTop) {
		double offsetY = fromTop ? -40 : 40;
		view.setTranslateY(offsetY);
		
		TranslateTransition tt = new TranslateTransition(Duration.millis(400), view);
		tt.setToY(0);
		tt.play();
	}
	
	private void animateReveal(ImageView view) {
		ScaleTransition shrink = new ScaleTransition(Duration.millis(300), view);
		shrink.setFromX(1);
		shrink.setToX(0);
		
		ScaleTransition expand = new ScaleTransition(Duration.millis(300), view);
		expand.setFromX(0);
		expand.setToX(1);
		
		SequentialTransition flip = new SequentialTransition(shrink, expand);
		flip.play();
	}
	
	private void showOutcome() {
		GameEngine.Outcome outcome = engine.getOutcome();
		
		switch(outcome) {
			case PLAYER_WIN:
				statusLabel.setText("You win!");
				playerWins++;
				break;
			case DEALER_WIN:
				statusLabel.setText("Dealer wins.");
				dealerWins++;
				break;
			case PUSH:
				statusLabel.setText("Push (tie).");
				break;
			default:
				statusLabel.setText("Round over.");
		}
		
		updateWinCountLabel();
		
		hitButton.setDisable(true);
		standButton.setDisable(true);
		dealButton.setDisable(true);
		playAgainButton.setDisable(false);
		
	}
	
	private void updateWinCountLabel() {
		dealerWinLabel.setText("Dealer Wins: " + dealerWins);
		playerWinLabel.setText("Player Wins: " + playerWins);
	}
	
	private ImageView createCardImage(Card card) {
		String rank = card.getRank().name().toLowerCase();
		String suit = card.getSuit().name().toLowerCase();
		
		String filename = "cards/" + rank + "_of_" + suit + ".png";
		//System.out.println(filename);
		//System.out.println(getClass().getResource(filename));
		Image image = new Image(getClass().getResourceAsStream(filename));
		ImageView view = new ImageView(image);
		
		view.setFitWidth(110);
		view.setPreserveRatio(true);
		
		return view;
	}
	
	private ImageView createBackCardImage() {
		Image image = new Image(getClass().getResourceAsStream("cards/back.png"));
		ImageView view = new ImageView(image);
		view.setFitWidth(110);
		view.setPreserveRatio(true);
		return view;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

}
