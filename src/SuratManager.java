import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.io.*;
import java.awt.Desktop;

public class SuratManager {
    private List<Surat> daftarSurat = new ArrayList<>();

    public SuratManager() {
        loadDummyData();
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public void tambahSurat(Surat surat) {
        daftarSurat.add(surat);
    }

    public void hapusSurat(int index) {
        if (index >= 0 && index < daftarSurat.size()) {
            daftarSurat.remove(index);
        }
    }

    public void updateSurat(int index, Surat surat) {
        if (index >= 0 && index < daftarSurat.size()) {
            daftarSurat.set(index, surat);
        }
    }

    public List<Surat> getDaftarSurat() {
        return daftarSurat;
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

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

    public List<Surat> sortByTanggal(List<Surat> list, boolean ascending) {
        return list.stream()
            .sorted(ascending
                ? Comparator.comparing(Surat::getTanggal)
                : Comparator.comparing(Surat::getTanggal).reversed())
            .collect(Collectors.toList());
    }

    // ── EXPORT PDF ────────────────────────────────────────────────────────────

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

        // Buka di browser default
        File htmlFile = new File(htmlPath);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(htmlFile.toURI());
        }
    }

    // ── DUMMY DATA ────────────────────────────────────────────────────────────

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
