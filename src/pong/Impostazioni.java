// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: le impostazioni. Scadenza: domenica 1 novembre 2026.
package pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Le impostazioni di una partita di Pong: quanti punti servono per vincere, con quale scarto
 * minimo, a che velocità va la palla. È un record (lezione 4): una volta create, non cambiano.
 *
 * IL PEZZO MANCANTE: il costruttore che rifiuta i valori sbagliati e il metodo leggi.
 * Ogni metodo ha il suo contratto, nel commento sopra. I test ufficiali (cartella test)
 * controllano i contratti.
 */
public record Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {

    /** Le impostazioni standard: 11 punti per vincere, scarto minimo 2, velocità della palla 5. */
    public static final Impostazioni STANDARD = new Impostazioni(11, 2, 5);

    /**
     * Il costruttore (lezione 7: un record che nasce giusto).
     * Promette: se i tre valori sono nei loro intervalli, le impostazioni con quei valori.
     * Lancia IllegalArgumentException se puntiPerVincere non è fra 1 e 99, se scartoMinimo
     * non è fra 1 e 10, o se velocitaPalla non è fra 1 e 20 (estremi compresi). Il messaggio
     * dice quale valore è sbagliato e perché.
     */
    public Impostazioni {
        if(puntiPerVincere < 1 || puntiPerVincere > 99){
            throw new IllegalArgumentException("puntiPerVincere deve essere tra 1 e 99, hai impostato: " + puntiPerVincere);
        }

        if(scartoMinimo < 1 || scartoMinimo > 10){
            throw new IllegalArgumentException("scartoMinimo deve essere tra 1 e 10, hai impostato: " + scartoMinimo);
        }

        if(velocitaPalla < 1 || velocitaPalla > 20){
            throw new IllegalArgumentException("velocitaPalla deve essere tra 1 e 20, hai impostato: " + velocitaPalla);
        }

    }


    /**
     * Legge le impostazioni dalle righe di un file, una impostazione per riga.
     * Pretende: righe non è null e non contiene null.
     * Promette:
     *  - le righe vuote (o di soli spazi) e quelle che, tolti gli spazi iniziali, cominciano
     *    con # sono ignorate;
     *  - ogni altra riga è nella forma  chiave = valore , con spazi qualsiasi attorno alla chiave,
     *    al segno = e al valore; le chiavi sono puntiPerVincere, scartoMinimo, velocitaPalla;
     *  - un'impostazione che nelle righe non c'è prende il valore standard;
     *  - il risultato sono le impostazioni lette.
     * Lancia IllegalArgumentException, con un messaggio che comincia con «riga N: » (N è il numero
     * della riga, contando da 1 tutte le righe, anche quelle vuote e i commenti), se in una riga:
     *  1. manca il segno = ;
     *  2. la chiave non è una delle tre;
     *  3. il valore non è un numero intero;
     *  4. la stessa chiave c'era già in una riga precedente;
     *  5. il valore è fuori dal suo intervallo (gli intervalli del costruttore).
     * Se ci sono errori in più righe, il messaggio è quello della riga più in alto.
     */
    public static Impostazioni leggi(List<String> righe) {
        int punti = STANDARD.puntiPerVincere();
        int scarto = STANDARD.scartoMinimo();
        int velocita = STANDARD.velocitaPalla();

        // indicatori per controllare la ripetizione delle chiavi
        boolean flagPunti = false;
        boolean flagScarto = false;
        boolean flagVelocita = false;

        for (int i=0; i < righe.size(); i++){
            int n = i+1;
            String rigaTrim = righe.get(i).trim();  // toglie gli spazi all'inizio e alla fine

            if (rigaTrim.isEmpty() || rigaTrim.startsWith("#")){  // righe vuote e che iniziano con #
                continue;
            }

            int sep = rigaTrim.indexOf('=');  // pos. dell'uguale
            if(sep == -1){
                throw new IllegalArgumentException("riga " + n + ": non è presente il segno '='");
            }

            // divido in chiave e valore
            String chiave = rigaTrim.substring(0, sep).trim();
            String valoreStringa = rigaTrim.substring(sep + 1).trim();   // parte dalla pos. dopo l'uguale

            // la chiave non è una delle tre consentite
            if(!chiave.equals("puntiPerVincere") && !chiave.equals("scartoMinimo") && !chiave.equals("velocitaPalla")){
                throw new IllegalArgumentException("riga " + n + ": chiave inserita non valida (" + chiave + ")");
            }

            // il valore non è un numero intero
            int valore;
            try {
                valore = Integer.parseInt(valoreStringa);
            } catch (NumberFormatException e){
                throw new IllegalArgumentException("riga " + n + ": il valore inserito non è un numero intero (" + valoreStringa + ")");
            }


            if(chiave.equals("puntiPerVincere")){
                if (flagPunti){
                    throw new IllegalArgumentException("riga " + n +": chiave ripetuta nella riga precedente (puntiPerVincere)");
                }

                if(valore < 1 || valore > 99){
                    throw new IllegalArgumentException("riga " + n + ": puntiPerVincere deve essere tra 1 e 99");
                }
                flagPunti = true;
                punti = valore;
            
            } else if(chiave.equals("scartoMinimo")){
                if (flagScarto){
                    throw new IllegalArgumentException("riga " + n +": chiave ripetuta nella riga precedente (scartoMinimo)");
                }

                if(valore < 1 || valore > 10){
                    throw new IllegalArgumentException("riga " + n + ": scartoMinimo deve essere tra 1 e 10");
                }
                flagScarto = true;
                scarto = valore;

            } else if(chiave.equals("velocitaPalla")){
                if (flagVelocita){
                    throw new IllegalArgumentException("riga " + n +": chiave ripetuta nella riga precedente (velocitaPalla)");
                }

                if(valore < 1 || valore > 20){
                    throw new IllegalArgumentException("riga " + n + ": velocitaPalla deve essere tra 1 e 20");
                }
                flagVelocita = true;
                velocita = valore;
            }
        }

        return new Impostazioni(punti, scarto, velocita);

    }

    /**
     * Legge le impostazioni da un file di testo.
     * Promette: come leggi, con le righe del file.
     * Lancia IOException se il file non si può leggere (per esempio non esiste): non la cattura,
     * la lascia a chi chiama. Lancia IllegalArgumentException come leggi.
     */
    public static Impostazioni daFile(Path file) throws IOException {
        return leggi(Files.readAllLines(file));
    }
}
