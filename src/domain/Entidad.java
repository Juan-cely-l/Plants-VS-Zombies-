    package domain;

    import javax.swing.*;

    import java.io.Serializable;

    public abstract class Entidad implements Serializable {
        protected int xPos; // Posición en la fila
        protected int yPos; // Posición en la columna
        protected int salud;
        protected  transient ImageIcon imagen;
        public Entidad(int xPos, int yPos, int salud) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.salud = salud;
        }

        // Métodos comunes
        public abstract void actualizar(); // Actualiza el estado de la entidad
        public abstract void interactuar(Entidad otra); // Interacción con otra entidad

        public int getxPos() {
            return xPos;
        }

        public int getyPos() {
            return yPos;
        }

        public int getSalud() {
            return salud;
        }

        public boolean estaMuerta() {
            return salud <= 0;
        }

        public void recibirDaño(int daño) {
            salud -= daño;
        }

        public void setxPos(int xPos) {
            this.xPos = xPos;
        }

        public void setyPos(int yPos) {
            this.yPos = yPos;
        }

        public boolean isMuerto() {
            return salud <= 0;
        }

        public void setSalud(int salud) {
            this.salud = salud;
        }

        public ImageIcon getImagen() {
            return imagen;
        }
    }
