import java.awt.*;
import java.awt.event.*;

class TicTacToe extends Frame {
   String turn = "O";	//value is either "O" or "X"
   Panel board;
   TicTacToeButton button[][];
   Label playerTurn;
   ButtonHandler bH = new ButtonHandler();
   int usedCells = 0;	//number of cells in use

   TicTacToe(String title) {
      super(title);
      board = new Panel();
      button = new TicTacToeButton[3][];
      playerTurn = new Label("Player One's Turn");
   }

   void launchGame() {
      board.setLayout(new GridLayout(3,3));
      /* initialize buttons */
      for (int i = 0, count = 0; i < 3; i++) {
         button[i] = new TicTacToeButton[3];
         for (int j = 0; j < 3; j++, count++) {
            button[i][j] = new TicTacToeButton();
            /* inialize button values to 0,1, 2,...,8 */
            button[i][j].value = (new Integer(count)).toString();
            board.add(button[i][j]);
            /* add listeners to the buttons */
            button[i][j].addActionListener(bH);
         }
      }
      add(board);
      add(playerTurn, BorderLayout.SOUTH);
      /* add listeners for closing the frame */
      addWindowListener( new WindowAdapter() {
         public void windowClosing( WindowEvent event ) {
            System.exit( 0 );
         }
      } );
      /* display */
      pack();
      setVisible(true);
   }

   class ButtonHandler implements ActionListener {
      void changeButtonLabel(TicTacToeButton b) {
         b.setLabel(turn);
         b.value = turn;
         /* remove listener */
         b.removeActionListener(bH);
         usedCells++;
      }

      //continued on next page
      public void actionPerformed(ActionEvent ae) {
         boolean win = false;
         boolean draw = false;
         int player = 0;
         TicTacToeButton b = (TicTacToeButton) ae.getSource();
         /* change content of button to O or X */
         changeButtonLabel(b);
         /* check for a win */
         /* horizontal */
         for (int i = 0; i < 3; i++) {
            if ((button[i][0].value).equals(button[i][1].value) &&
                (button[i][0].value).equals(button[i][2].value)) {
               win = true;
            }
         }
         /* vertical */
         for (int i = 0; i < 3; i++) {
            if ((button[0][i].value).equals(button[1][i].value) &&
                (button[0][i].value).equals(button[2][i].value)) {
               win = true;
            }
         }
         /* diagonal */
         if ((button[0][0].value).equals(button[1][1].value) &&
             (button[0][0].value).equals(button[2][2].value)) {
            win = true;
         } else if ((button[0][2].value).equals(button[1][1].value) &&
             (button[0][2].value).equals(button[2][0].value)) {
            win = true;
	}
         /* check for a draw */
         if (!win) {
            if (usedCells==9) {
               draw = true;
            }
         }
         /* Change message */
         String message = "";

         //continued on next page
         if (win) {
            if (turn.equals("O")) {
               player = 1;
            } else {
               player = 2;
            }
            message += "Player " + player + " wins!";
            /* remove all listeners */
            for (int i = 0; i < 3; i++) {
               for  (int j = 0; j < 3; j++) {
                  button[i][j].removeActionListener(bH);
               }
            }
         } else if (draw) {
            message += "It's a draw!";
            for (int i = 0; i < 3; i++) {
               for  (int j = 0; j < 3; j++) {
                  button[i][j].removeActionListener(bH);
               }
            }
         } else {
            /* change turn */
            if (turn.equals("O")) {
               turn = new String("X");
               player = 2;
               message += "Player Two's Turn.";
            } else {
               turn = new String("O");
               player = 1;
               message += "Player One's Turn.";
            }
         }
         playerTurn.setText(message);
      }
   }

   class TicTacToeButton extends Button {
      String value;
   }

   public static void main(String args[]) {
      TicTacToe game = new TicTacToe("Tic-Tac-Toe");
      game.launchGame();
   }
}