import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.io.*;
import java.awt.Desktop;

/**
 * Kelas SuratManager bertanggung jawab mengelola seluruh data surat,
 * termasuk operasi CRUD, filter, sorting, export, dan data dummy.
 *
 * @author Kelompok UAS PBO
 */
public class SuratManager {

    /** Daftar surat yang tersimpan dalam memori */
    private List<Surat> daftarSurat = new ArrayList<>();

    /**
     * Konstruktor SuratManager.
     * Otomatis memuat data dummy saat objek dibuat.
     */
    public SuratManager() {
        loadDummyData();
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    /**
     * Menambahkan surat baru ke dalam daftar.
     *
     * @param surat objek Surat yang akan ditambahkan
     */
    public void tambahSurat(Surat surat) {
        daftarSurat.add(surat);
    }

    /**
     * Menghapus surat berdasarkan index.
     *
     * @param index posisi surat dalam daftar (dimulai dari 0)
     */
    public void hapusSurat(int index) {
        if (index >= 0 && index < daftarSurat.size()) {
            daftarSurat.remove(index);
        }
    }

    /**
     * Memperbarui data surat pada index tertentu.
     *
     * @param index posisi surat yang akan diperbarui
     * @param surat objek Surat baru sebagai pengganti
     */
    public void updateSurat(int index, Surat surat) {
        if (index >= 0 && index < daftarSurat.size()) {
            daftarSurat.set(index, surat);
        }
    }

    /**
     * Mengambil seluruh daftar surat yang tersimpan.
     *
     * @return List berisi semua objek Surat
     */
    public List<Surat> getDaftarSurat() {
        return daftarSurat;
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    /**
     * Memfilter daftar surat berdasarkan keyword dan jenis surat.
     * Pencarian keyword mencakup nomor surat, perihal, dan pengirim/penerima.
     *
     * @param keyword kata kunci pencarian (bisa null atau kosong)
     * @param jenis   jenis surat MASUK/KELUAR (null = semua jenis)
     * @return List surat yang sesuai dengan filter
     */
    public List<Surat> filter(String keyword, Surat.JenisSurat jenis) {
        return daftarSurat.stream()
            .filter(s -> {
                boolean cocokJenis = (jenis == null) || s.getJenis() == jenis;
                boolean cocokKeyword = keyword == null || keyword.isEmpty()
                    || s.getNomorSurat().toLowerCase().contains(keyword.toLowerCase())
                    || s.getPerihal().toLowerCase().contains(keyword.toLowerCase())
                    || s.getPengirimPenerima().toLowerCase().contains(keyword.toLowerCase());
                return cocokJenis && cocokKeyword;
            })
            .collect(Collectors.toList());
    }

    // ── SORT ──────────────────────────────────────────────────────────────────

    /**
     * Mengurutkan daftar surat berdasarkan tanggal.
     *
     * @param list      List surat yang akan diurutkan
     * @param ascending true = terlama ke terbaru, false = terbaru ke terlama
     * @return List surat yang sudah diurutkan
     */
    public List<Surat> sortByTanggal(List<Surat> list, boolean ascending) {
        return list.stream()
            .sorted(ascending
                ? Comparator.comparing(Surat::getTanggal)
                : Comparator.comparing(Surat::getTanggal).reversed())
            .collect(Collectors.toList());
    }

    // ── EXPORT PDF ────────────────────────────────────────────────────────────

    /**
     * Mengekspor daftar surat ke file HTML yang dapat dicetak sebagai PDF.
     * File HTML akan otomatis dibuka di browser default.
     *
     * @param list     List surat yang akan diekspor
     * @param filePath path tujuan file (ekstensi .pdf akan diganti .html)
     * @throws Exception jika terjadi kesalahan saat menulis atau membuka file
     */
    public void exportToPdf(List<Surat> list, String filePath) throws Exception {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("h1 { color: #333; text-align: center; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        html.append("th { background: #4a90d9; color: white; padding: 10px; text-align: left; }");
        html.append("td { padding: 8px 10px; border-bottom: 1px solid #ddd; }");
        html.append("tr:nth-child(even) { background: #f5f5f5; }");
        html.append(".masuk { color: #27ae60; font-weight: bold; }");
        html.append(".keluar { color: #e74c3c; font-weight: bold; }");
        html.append("</style></head><body>");
        html.append("<h1>Laporan Surat Masuk & Keluar</h1>");
        html.append("<p style='text-align:center'>Tanggal cetak: ").append(LocalDate.now()).append("</p>");
        html.append("<table>");
        html.append("<tr><th>No</th><th>Nomor Surat</th><th>Perihal</th>");
        html.append("<th>Pengirim/Penerima</th><th>Tanggal</th><th>Jenis</th><th>Keterangan</th></tr>");

        // Iterasi setiap surat dan masukkan ke baris tabel HTML
        int no = 1;
        for (Surat s : list) {
            String cls = s.getJenis() == Surat.JenisSurat.MASUK ? "masuk" : "keluar";
            html.append("<tr>");
            html.append("<td>").append(no++).append("</td>");
            html.append("<td>").append(s.getNomorSurat()).append("</td>");
            html.append("<td>").append(s.getPerihal()).append("</td>");
            html.append("<td>").append(s.getPengirimPenerima()).append("</td>");
            html.append("<td>").append(s.getTanggalFormatted()).append("</td>");
            html.append("<td class='").append(cls).append("'>").append(s.getJenisString()).append("</td>");
            html.append("<td>").append(s.getKeterangan()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table></body></html>");

        // Simpan sebagai HTML dulu lalu buka di browser (bisa print to PDF)
        String htmlPath = filePath.replace(".pdf", ".html");
        try (PrintWriter pw = new PrintWriter(new FileWriter(htmlPath))) {
            pw.print(html.toString());
        }

        // Buka file HTML di browser default sistem
        File htmlFile = new File(htmlPath);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(htmlFile.toURI());
        }
    }

    // ── DUMMY DATA ────────────────────────────────────────────────────────────

    /**
     * Memuat 10 data surat dummy untuk keperluan demonstrasi aplikasi.
     * Terdiri dari 5 surat masuk dan 5 surat keluar tahun 2024.
     */
    private void loadDummyData() {
        daftarSurat.add(new Surat("SM-001/2024", "Undangan Rapat Koordinasi",
            "Kementerian Pendidikan", LocalDate.of(2024, 1, 5),
            Surat.JenisSurat.MASUK, "Rapat koordinasi bulanan"));

        daftarSurat.add(new Surat("SK-001/2024", "Permohonan Dana Operasional",
            "Bagian Keuangan", LocalDate.of(2024, 1, 10),
            Surat.JenisSurat.KELUAR, "Permohonan dana Q1 2024"));

        daftarSurat.add(new Surat("SM-002/2024", "Pemberitahuan Libur Nasional",
            "BKN Pusat", LocalDate.of(2024, 2, 3),
            Surat.JenisSurat.MASUK, "Libur nasional Februari"));

        daftarSurat.add(new Surat("SK-002/2024", "Laporan Kegiatan Semester 1",
            "Kepala Dinas", LocalDate.of(2024, 2, 15),
            Surat.JenisSurat.KELUAR, "Laporan kegiatan Januari-Februari"));

        daftarSurat.add(new Surat("SM-003/2024", "Surat Tugas Dinas Luar",
            "Biro SDM", LocalDate.of(2024, 3, 1),
            Surat.JenisSurat.MASUK, "Dinas luar kota Maret 2024"));

        daftarSurat.add(new Surat("SK-003/2024", "Pengajuan Cuti Tahunan",
            "Bagian Kepegawaian", LocalDate.of(2024, 3, 20),
            Surat.JenisSurat.KELUAR, "Cuti tahunan pegawai"));

        daftarSurat.add(new Surat("SM-004/2024", "Undangan Seminar Nasional",
            "Universitas Indonesia", LocalDate.of(2024, 4, 8),
            Surat.JenisSurat.MASUK, "Seminar teknologi informasi"));

        daftarSurat.add(new Surat("SK-004/2024", "Balasan Undangan Rapat",
            "Kementerian Keuangan", LocalDate.of(2024, 4, 12),
            Surat.JenisSurat.KELUAR, "Konfirmasi kehadiran rapat"));

        daftarSurat.add(new Surat("SM-005/2024", "Instruksi Kerja Pegawai",
            "Sekretariat Jenderal", LocalDate.of(2024, 5, 2),
            Surat.JenisSurat.MASUK, "Instruksi kerja Q2 2024"));

        daftarSurat.add(new Surat("SK-005/2024", "Permohonan Izin Penelitian",
            "Lembaga Penelitian", LocalDate.of(2024, 5, 17),
            Surat.JenisSurat.KELUAR, "Izin penelitian lapangan"));
    }
}