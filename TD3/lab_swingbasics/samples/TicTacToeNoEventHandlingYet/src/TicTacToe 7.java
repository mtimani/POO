import java.awt.*;
class TicTacToe extends Frame {
   Panel board;
   Button button[];
   Label playerTurn;
   TicTacToe(String title) {
      super(title);
      board = new Panel();
      button = new Button[9];
      playerTurn = new Label("Player One's Turn");
   }
   void launchGame() {
      board.setLayout(new GridLayout(3,3));
      for (int i = 0; i < 9; i++) {
         button[i] = new Button();
         board.add(button[i]);
      }
      add(board);
      add(playerTurn, BorderLayout.SOUTH);
      pack();
      setVisible(true);
   }
   public static void main(String args[]) {
      TicTacToe game = new TicTacToe("Tic-Tac-Toe");
      game.launchGame();
   }
}