package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static final double CONCERT_A = 440.0;
    public static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static void main(String[] args) {
        /* create all guitar strings */
        GuitarString[] string = new GuitarString[keyboard.length()];
        for (int i = 0; i < keyboard.length(); i++) {
            string[i] = new GuitarString(CONCERT_A * Math.pow(2, (i - 24) / 12));
        }

        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if(keyboard.indexOf(key) != -1){
                    string[keyboard.indexOf(key)].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0.;
            for (GuitarString gS : string) {
                 sample += gS.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString gS : string) {
                gS.tic();
            }
        }
    }
}
