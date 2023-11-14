package edu.epsevg.prop.lab.c4;

/**
 * Jugador aleatori
 * "Alea jacta est"
 * @author Profe
 */
public class Connecta4
  implements Jugador, IAuto
{
  private String nom;
  private int color_fitxes;
  private int nodes;
  
  public Connecta4()
  {
    nom = "Connecta4";
  }
  
  public int heuristica2(Tauler t, int columna, int color){
      int heur = 0;
      int i = t.getMida()-1;
      int color_fila = t.getColor(i, columna);
      while (color_fila == 0){
          --i;
          color_fila = t.getColor(i, columna);
      }
      //System.out.println("Colocado " + color_fila + " en " + i + "," + columna);
      
      //Contar
      int fitxes = 0;
      int j = i - 1;
      while (j >= 0 && t.getColor(j, columna) == color_fila){
          fitxes++;
          j--;
      }
      
      int x = i - 1;
      int y = columna - 1;
      while (x >= 0 && y >= 0 && t.getColor(x, y) == color_fila){
          fitxes++;
          x--;
          y--;
      }
      
      x = i - 1;
      y = columna + 1;
      while (x >= 0 && y < t.getMida() && t.getColor(x, y) == color_fila){
          fitxes++;
          x--;
          y++;
      }
      int col = columna - 1;
      while (col >= 0 && t.getColor(i, col) == color_fila){
          fitxes++;
          col--;
      }
      
      col = columna + 1;
      while (col < t.getMida() && t.getColor(i, col) == color_fila){
          fitxes++;
          col++;
      }
      
      //t.pintaTaulerALaConsola();
      //System.out.println("fichas: " + fitxes);
      //System.out.println("-------------------------");
      
      return fitxes;
  }
  
  public int heuristica(Tauler t, int columna, int color){
  
      if (color_fitxes == color && t.solucio(columna, color)) return Integer.MAX_VALUE;
      else if (color_fitxes != color && t.solucio(columna, color)) return Integer.MIN_VALUE;
      else{
        int i = t.getMida()-1;
        while (t.getColor(i, columna) == 0){
            --i;
        } 
        int heur = 0;
        for (int j = 0; j < t.getMida(); ++j){
            if (t.getColor(i, j) != 0) ++heur;
        }
        //t.pintaTaulerALaConsola();
        //System.out.println("heuristica: " + heur);
        //System.out.println("-------------------------");
        ++this.nodes;
        return heur;
      }
  }
  
  public int moviment_recursiva(Tauler t, int color, int profunditat, int ultmov, int alpha, int beta){
      if (ultmov >= 0 && (t.solucio(ultmov, color*-1) || !t.espotmoure() || profunditat >= 8)){
          return heuristica(t, ultmov, color*-1);
      }
      else {
        int millor_col = 0;
        for (int col = 0; col < t.getMida(); ++col){
            if (t.movpossible(col) && beta > alpha){
                Tauler t2 = new Tauler(t);
                t2.afegeix(col, color);
                if (profunditat % 2 == 0){
                    int alpha_in = alpha;
                    alpha = Math.max(alpha, moviment_recursiva(t2, color * -1, profunditat + 1, col,alpha, beta));
                    if (alpha != alpha_in) millor_col = col;
                }
                else {
                    beta = Math.min(beta, moviment_recursiva(t2, color * -1, profunditat + 1, col,alpha, beta));
                }
            }
        }
        if (ultmov == -1){
            return millor_col;
        } 
        else {
            int v = 0;
            if (profunditat % 2 == 0) v = alpha;
            else v = beta;
            return v;
        }
      }
  }
  
  @Override
  public int moviment(Tauler t, int color)
  {
      //Tauler t2 = new Tauler(4);
      this.nodes = 0;
      int a = moviment_recursiva(t, color, 0, -1, -1000, 1000);
      System.out.println(nodes);
      return a;
      /*
      for (int col = 0; col < t.getMida(); ++col){
          if (t.movpossible(col)){
              Tauler t2 = new Tauler(t);
              t2.afegeix(col, color);
              t2.pintaTaulerALaConsola();
              System.out.println("-------------------------");
          }
      }
      
      return 0;*/
  }
  
  @Override
  public String nom()
  {
    return nom;
  }
}


