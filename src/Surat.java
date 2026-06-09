import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Surat {
    public enum JenisSurat {
        MASUK, KELUAR
    }

    private String nomorSurat;
    private String perihal;
    private String pengirimPenerima;
    private LocalDate tanggal;
    private JenisSurat jenis;
    private String keterangan;

    public Surat(String nomorSurat, String perihal, String pengirimPenerima,
                 LocalDate tanggal, JenisSurat jenis, String keterangan) {
        this.nomorSurat = nomorSurat;
        this.perihal = perihal;
        this.pengirimPenerima = pengirimPenerima;
        this.tanggal = tanggal;
        this.jenis = jenis;
        this.keterangan = keterangan;
    }

    public String getNomorSurat()       { return nomorSurat; }
    public String getPerihal()          { return perihal; }
    public String getPengirimPenerima() { return pengirimPenerima; }
    public LocalDate getTanggal()       { return tanggal; }
    public JenisSurat getJenis()        { return jenis; }
    public String getKeterangan()       { return keterangan; }

    public void setNomorSurat(String nomorSurat)             { this.nomorSurat = nomorSurat; }
    public void setPerihal(String perihal)                   { this.perihal = perihal; }
    public void setPengirimPenerima(String pengirimPenerima) { this.pengirimPenerima = pengirimPenerima; }
    public void setTanggal(LocalDate tanggal)                { this.tanggal = tanggal; }
    public void setJenis(JenisSurat jenis)                   { this.jenis = jenis; }
    public void setKeterangan(String keterangan)             { this.keterangan = keterangan; }

    public String getTanggalFormatted() {
        return tanggal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getJenisString() {
        return jenis == JenisSurat.MASUK ? "Surat Masuk" : "Surat Keluar";
    }
}
