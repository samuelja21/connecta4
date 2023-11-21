package edu.epsevg.prop.lab.c4;

/**
 * Jugador C4
 * 
 * @author Samuel i Ivan
 */
public class C4
  implements Jugador, IAuto
{
  private String nom;
  private int color_fitxes;
  private int nodes;
  private int nivell_max;
  private boolean alphaBeta;
  
    /**
     *nivell és la profunditat màxima a la que arriba el minimax, i poda és true si volem tenir poda alpha beta, false en cas contrari
     * @param nivell
     * @param poda
     */
    public C4(int nivell, boolean poda)
  {
    nom = "C4";
    nivell_max = nivell;
    alphaBeta = poda;
  }
  
    /**
     *Retorna el valor de la heurística pel taulell t, on ultmov i colr són l'última fitxa que s'ha col·locat i la se va columna
     * @param t
     * @param ultmov
     * @param colr
     * @return valor heurística
     */
    public int heuristica(Tauler t, int ultmov, int colr){
    int h = 0;
    if (t.solucio(ultmov, colr)){
        if (colr != color_fitxes) h = Integer.MIN_VALUE+1;
        else h =  Integer.MAX_VALUE-1;
    }
    else {
        //COLUMNES
        for (int col = 0; col < t.getMida(); ++col){
            int ratxa = 0;
            int fila = t.getMida()-1;
            int color = t.getColor(fila, col);
            int buides = 0;
            while (color == 0 && fila > 0){
                ++buides;
                --fila;
                color = t.getColor(fila, col);
            }
            if (color != 0){
                while (fila >= 0 && t.getColor(fila, col) == color){
                    if (color == color_fitxes) ++ratxa;
                    else --ratxa;
                    --fila;
                }
            }
            else ++buides;
            if (buides + Math.abs(ratxa) >= 4){
                    if (ratxa > 0) h += Math.pow(ratxa, ratxa);
                    else if (ratxa < 0) h -= Math.pow(Math.abs(ratxa), Math.abs(ratxa));
            }
        }

        //FILES
        for (int fila = 0; fila < t.getMida(); ++fila){
            boolean buida = false;
            for (int col = 0; col < t.getMida() - 3 && !buida; ++col){
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
                if (buides == 4) buida = true;
            }
        }
    }

    
    //t.pintaTaulerALaConsola();
    //System.out.println("heuristica = " + h);
    //System.out.println("----------------------");
    this.nodes++;
      
    return h;
  }
  
    /**
     *Retorna la millor columna si ultmov = -1, sinó, retorna el valor heurístic del node, resultat de cridar al mètode heuristica si és una fulla o
     * del valor mínim o màxim dels seus fills, si és un node diferent a l'arrel.
     * @param t
     * @param color
     * @param profunditat
     * @param ultmov
     * @param alpha
     * @param beta
     * @return millor columna / heurística del node
     */
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
      return millor_columna;
  }
  
    /**
     *Retorna el moviment escollit, és a dir, la columna resultat de cridar a moviment_recursiva.
     * @param t
     * @param color
     * @return columna escollida
     */
    @Override
  public int moviment(Tauler t, int color)
  {
      this.nodes = 0;
      this.color_fitxes = color;
      int mov = moviment_recursiva(t, color, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE);
      System.out.println(this.nodes + " nodes visitats.");
      return mov;
  }
  
    /**
     *Retorna el nom del jugador
     * @return nom
     */
    @Override
  public String nom()
  {
    return nom;
  }
}



