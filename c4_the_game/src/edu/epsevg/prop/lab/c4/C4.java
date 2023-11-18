package edu.epsevg.prop.lab.c4;

/**
 * Jugador aleatori
 * "Alea jacta est"
 * @author Profe
 */
public class C4
  implements Jugador, IAuto
{
  private String nom;
  private int color_fitxes;
  private int nodes;
  private int nivell_max;
  private boolean alphaBeta;
  private boolean primerMov;
  
  public C4(int nivell, boolean poda)
  {
    nom = "C4";
    nivell_max = nivell;
    alphaBeta = poda;
  }
  
  public int heuristica(Tauler t, int ultmov, int colr){
    if (t.solucio(ultmov, colr)){
        if (colr != color_fitxes) return Integer.MIN_VALUE+1;
        else return Integer.MAX_VALUE-1;
    }
    int h = 0;
    
    //COLUMNES
    for (int col = 0; col < t.getMida(); ++col){
        int ratxa = 0;
        int fila = t.getMida()-1;
        int color = t.getColor(fila, col);
        while (color == 0 && fila > 0){
            --fila;
            color = t.getColor(fila, col);
        }
        int buides = t.getMida()-1-fila;
        if (color != 0){
            while (fila >= 0 && t.getColor(fila, col) == color){
                if (color == color_fitxes) ++ratxa;
                else --ratxa;
                --fila;
            }
        }
        if (buides + Math.abs(ratxa) >= 4){
                if (ratxa > 0) h += Math.pow(ratxa, ratxa);
                else if (ratxa < 0) h -= Math.pow(Math.abs(ratxa), Math.abs(ratxa));
        }
    }
    
    //FILES
    for (int fila = 0; fila < t.getMida(); ++fila){
        boolean vuida = false;
        for (int col = 0; col < t.getMida() - 3 && !vuida; ++col){
            int buides = 0;
            int fitxes_jugador = 0;
            int fitxes_rival = 0;
            boolean possible4 = true;
            int consecutives = 0;
            int color = 0;
            int ratxa = 0;
            for (int i = 0; i < 4 && possible4; ++i){
                if (consecutives == 0 && t.getColor(fila, col+i) != 0) consecutives = 1;
                else if (color == t.getColor(fila, col+i))++consecutives;
                if (t.getColor(fila, col+i) == color_fitxes) fitxes_jugador++;
                else if (t.getColor(fila, col+i) == 0){
                    ratxa = consecutives;
                    buides++;
                    if (fila > 0 && t.getColor(fila-1, col+i) == 0) possible4 = false;
                } 
                else fitxes_rival++;
                if (fitxes_jugador > 0 && fitxes_rival > 0) possible4 = false;
                color = t.getColor(fila, col+i);
            }
            if (possible4){
                ratxa = Math.max(ratxa, consecutives);
                if (fitxes_jugador > 0) h += Math.pow(fitxes_jugador, fitxes_jugador) + 2*ratxa;
                else if (fitxes_rival > 0) h -= Math.pow(fitxes_rival, fitxes_rival)+2*ratxa;
            }
            if (buides == 4) vuida = true;
        }
    }
    
    //t.pintaTaulerALaConsola();
    //System.out.println("heuristica = " + h);
    //System.out.println("----------------------");
    this.nodes++;
      
    return h;
  }
  
  public int moviment_recursiva(Tauler t, int color, int profunditat, int ultmov, int alpha, int beta){
      int millor_columna = 0;
      if (ultmov >= 0 && (profunditat >= nivell_max || !t.espotmoure() || t.solucio(ultmov, color))){
        return heuristica(t, ultmov, color*-1);
      }
      else {
        int heuristica = 0;
        for (int i = 0; i < t.getMida(); ++i){
            if (t.movpossible(i)){
                if (!alphaBeta || (alphaBeta && beta > alpha)){
                    Tauler t2 = new Tauler(t);
                    t2.afegeix(i, color);
                    if (profunditat % 2 == 0) {
                        heuristica = moviment_recursiva(t2, color*-1, profunditat+1, i, alpha, beta);
                        if (heuristica > alpha) {
                            alpha = heuristica;
                            millor_columna = i;
                        }
                    }
                    else {
                       heuristica = moviment_recursiva(t2, color*-1, profunditat+1, i, alpha, beta); 
                       if (beta > heuristica){
                           beta = heuristica;
                       }
                    }   
                }
            }   
        }
        if (ultmov >= 0){
            if (profunditat % 2 == 0) return alpha;
            else return beta;
        }
      }
      if (ultmov == -1)System.out.println("heuristica: " + alpha);
      return millor_columna;
  }
  
  @Override
  public int moviment(Tauler t, int color)
  {
      this.nodes = 0;
      this.color_fitxes = color;
      boolean primer = true;
      int col = 0;
      while (col < t.getMida() && primer){
          if (t.getColor(0, col) != 0) primer = false;
          ++col;
      }
      primerMov = primer;
      int mov = moviment_recursiva(t, color, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE);
      System.out.println(this.nodes + " nodes visitats.");
      return mov;
  }
  
  @Override
  public String nom()
  {
    return nom;
  }
}

