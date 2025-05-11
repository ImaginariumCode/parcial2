public class RegistroTemperatura {
    private String ciudad;
    private java.time.LocalDate fecha;
    private double temperatura;

    public RegistroTemperatura(String ciudad, java.time.LocalDate fecha, double temperatura) {
        this.ciudad = ciudad;
        this.fecha = fecha;
        this.temperatura = temperatura;
    }

    public String getCiudad() {
        return ciudad;
    }

    public java.time.LocalDate getFecha() {
        return fecha;
    }

    public double getTemperatura() {
        return temperatura;
    }
} 