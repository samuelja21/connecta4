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
        int ultima_buida = t.getMida() - buides;
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



/*
public int heuristica(Tauler t, int ultmov, int colr) {
    
    int h = 0;
    int ratxa;
    boolean c4 = false;
    
    for (int f = 0; f < t.getMida() & !c4; ++f) {
        ratxa = comproba_fila(t, f, colr);
        if (ratxa == Integer.MAX_VALUE) {
            h = ratxa;
            c4 = true;
        }
        else h += ratxa;
    }
    for (int c = 0; c < t.getMida() & !c4; ++c) {
        ratxa = comproba_columna(t, c, colr);
        if (ratxa == Integer.MAX_VALUE) {
            h = ratxa;
            c4 = true;
        }
        else h += ratxa;
    }
    for (int f = 1; f < t.getMida()-3 & !c4; ++f) {
        ratxa = comproba_diagonal1(t, f, 0, colr);
        if (ratxa == Integer.MAX_VALUE) {
            h = ratxa;
            c4 = true;
        }
        else h += ratxa;
    }
    for (int c = 0; c < t.getMida()-3 & !c4; ++c) {
        ratxa = comproba_diagonal1(t, 0, c, colr);
        if (ratxa == Integer.MAX_VALUE) {
            h = ratxa;
            c4 = true;
        }
        else h += ratxa;
    }
    for (int f = t.getMida()-4; f > 0 & !c4; --f) {
        ratxa = comproba_diagonal2(t, f, 0, colr);
        if (ratxa == Integer.MAX_VALUE) {
            h = ratxa;
            c4 = true;
        }
        else h += ratxa;
    }
    for (int c = t.getMida()-1; c > 3 & !c4; --c) {
        ratxa = comproba_diagonal2(t, 0, c, colr);
        if (ratxa == Integer.MAX_VALUE) {
            h = ratxa;
            c4 = true;
        }
        else h += ratxa;
    }
    
    this.nodes++;
    return h;
}

public int comproba_fila(Tauler t, int f, int colr){
    int ratxa = 0;
    int h = 0;
    int fitxes = 0;
    int consec_iguals = 0;
    boolean sortir = false;
    
    for (int c = 0; c < t.getMida() & !sortir; ++c) {
        if (t.getColor(f, c) != -colr) {
            ++fitxes;
            if (t.getColor(f, c) == colr) {
                ++consec_iguals;
                ratxa += 2 * consec_iguals;
                
                if (consec_iguals == 4) {
                    sortir = true;
                    ratxa = Integer.MAX_VALUE;
                }
            }
            else {
                consec_iguals = 0;
                ratxa += 1;
            }
        }
        else {
            if (fitxes > 3) h += ratxa; 
            if (c > 3) sortir = true;
            fitxes = 0;
            ratxa = 0;
            consec_iguals = 0;
        }
    }
    if (fitxes > 4) h += ratxa;
    
    return h;
}

public int comproba_columna(Tauler t, int c, int colr){
    int ratxa = 0;
    int fitxes = 0;
    int consec_iguals = 0;
    boolean sortir = false;
    
    for (int f = 0; f < t.getMida() & !sortir; ++f) {
        if (t.getColor(f, c) != 0) {
            ++fitxes;
            if (t.getColor(f, c) == colr) {
                ++consec_iguals;
                ratxa += 2 * consec_iguals;
                
                if (consec_iguals == 4) {
                    ratxa = Integer.MAX_VALUE;
                    sortir = true;
                }
            }
            else {
                if (f > 3) sortir = true;
                consec_iguals = 0;
                ratxa = 0;
            }
        }
        else {
            ratxa += 1 * ((t.getMida()-1)-fitxes);
            sortir = true;
        }
    }
    return ratxa;
}

public int comproba_diagonal1(Tauler t, int f, int c, int colr){
    int ratxa = 0;
    int h = 0;
    int fitxes = 0;
    int consec_iguals = 0;
    boolean sortir = false;
    
    while (f < t.getMida() & c < t.getMida() & !sortir) {
        if (t.getColor(f, c) != -colr) {
            ++fitxes;
            if (t.getColor(f, c) == colr) {
                ++consec_iguals;
                ratxa += 2 * consec_iguals;
                
                if (consec_iguals == 4) {
                    sortir = true;
                    ratxa = Integer.MAX_VALUE;
                }
            }
            else {
                consec_iguals = 0;
                ratxa += 1;
            }
        }
        else {
            if (fitxes > 3) h += ratxa; 
            if (c > 3 | f > 3) sortir = true;
            fitxes = 0;
            ratxa = 0;
            consec_iguals = 0;
        }
        ++f;
        ++c;
    }
        
    if (fitxes > 4) h += ratxa;
    
    return h;
}

public int comproba_diagonal2(Tauler t, int f, int c, int colr){
    int ratxa = 0;
    int h = 0;
    int fitxes = 0;
    int consec_iguals = 0;
    boolean sortir = false;
    
    while (f < t.getMida() & c >= 0 & !sortir) {
        if (t.getColor(f, c) != -colr) {
            ++fitxes;
            if (t.getColor(f, c) == colr) {
                ++consec_iguals;
                ratxa += 2 * consec_iguals;
                
                if (consec_iguals == 4) {
                    sortir = true;
                    ratxa = Integer.MAX_VALUE;
                }
            }
            else {
                consec_iguals = 0;
                ratxa += 1;
            }
        }
        else {
            if (fitxes > 3) h += ratxa; 
            if (c < 3 | f > 3) sortir = true;
            fitxes = 0;
            ratxa = 0;
            consec_iguals = 0;
        }
        ++f;
        --c;
    }
        
    if (fitxes > 4) h += ratxa;
    
    return h;
}
*/
}

