package reproductormp3;

import javazoom.jl.player.Player;
import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Hugo Arvizu
 */
public class ReproductorMP3 extends JFrame {

    /**
     * @param args the command line arguments
     */
    private JButton btnAnterior, btnPlay, btnPausa, btnSiguiente, btnStop;
    private JLabel lblCancion;
    private ArrayList<String> listaCanciones;
    private int indiceActual = 0;
    private Player player;
    private Thread hiloReproduccion;
    private boolean enPausa = false;
    private FileInputStream fis;
    private long pausaLocation;
    private long totalLength;

    public ReproductorMP3() {
        super("🎧 Reproductor MP3 Simple");

        // ---- Lista de canciones ----        
        listaCanciones = new ArrayList <>();
        listaCanciones.add("C:\\Users\\Hector Gael\\Documents\\NetBeansProjects\\ReproductorMp3\\src\\music\\The Ronettes - Be My Baby (Official Audio).mp3");
        listaCanciones.add("C:\\Users\\Hector Gael\\Documents\\NetBeansProjects\\ReproductorMp3\\src\\music\\V 'Love Me Again' Official MV.mp3");
        listaCanciones.add("C:\\Users\\Hector Gael\\Documents\\NetBeansProjects\\ReproductorMp3\\src\\music\\j-hope Killin' It Girl (Solo Version) Official MV (Choreography ver.).mp3");
        

        //Interfaz
        lblCancion = new JLabel("Selecciona una canción", SwingConstants.CENTER);
        btnAnterior = new JButton("⏮ Anterior");
        btnPlay = new JButton("▶ Play");
        btnPausa = new JButton("⏸ Pausa");
        btnSiguiente = new JButton("⏭ Siguiente");
        btnStop = new JButton("⏹ Stop");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAnterior);
        panelBotones.add(btnPlay);
        panelBotones.add(btnPausa);
        panelBotones.add(btnSiguiente);
        panelBotones.add(btnStop);

        setLayout(new BorderLayout());
        add(lblCancion, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        setSize(500, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ---- Eventos ----
        btnPlay.addActionListener(e -> reproducir());
        btnPausa.addActionListener(e -> pausar());
        btnStop.addActionListener(e -> detener());
        btnSiguiente.addActionListener(e -> siguiente());
        btnAnterior.addActionListener(e -> anterior());
    }

    private void reproducir() {
        try {
            if (enPausa) {
                // Reanudar desde pausa
                fis = new FileInputStream(listaCanciones.get(indiceActual));
                fis.skip(totalLength - pausaLocation);
                player = new Player(fis);
                enPausa = false;
            } else {
                // Reproducir desde el inicio
                detener();
                fis = new FileInputStream(listaCanciones.get(indiceActual));
                totalLength = fis.available();
                player = new Player(fis);
                lblCancion.setText("Reproduciendo: " + new File(listaCanciones.get(indiceActual)).getName());
            }

            hiloReproduccion = new Thread(() -> {
                try {
                    player.play();
                } catch (Exception ex) {
                    System.out.println("Error al reproducir: " + ex.getMessage());
                }
            });
            hiloReproduccion.start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void pausar() {
        try {
            if (player != null) {
                pausaLocation = fis.available();
                player.close();
                enPausa = true;
                lblCancion.setText("Pausado: " + new File(listaCanciones.get(indiceActual)).getName());
            }
        } catch (Exception ex) {
            System.out.println("Error al pausar: " + ex.getMessage());
        }
    }

    private void detener() {
        try {
            if (player != null) {
                player.close();
                hiloReproduccion = null;
                lblCancion.setText("Detenido");
            }
        } catch (Exception ex) {
            System.out.println("Error al detener: " + ex.getMessage());
        }
    }

    private void siguiente() {
        detener();
        indiceActual = (indiceActual + 1) % listaCanciones.size();
        reproducir();
    }

    private void anterior() {
        detener();
        indiceActual = (indiceActual - 1 + listaCanciones.size()) % listaCanciones.size();
        reproducir();
    }

    public static void main(String[] args) {
        // TODO code application logic here
        SwingUtilities.invokeLater(() -> new ReproductorMP3().setVisible(true));
    }
}
