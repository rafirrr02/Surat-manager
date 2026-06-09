import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainApp extends JFrame {

    private SuratManager manager = new SuratManager();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cbFilter;
    private JComboBox<String> cbSort;
    private List<Surat> currentList;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color COLOR_PRIMARY  = new Color(41, 128, 185);
    private static final Color COLOR_SUCCESS  = new Color(39, 174, 96);
    private static final Color COLOR_DANGER   = new Color(231, 76, 60);
    private static final Color COLOR_BG       = new Color(245, 247, 250);
    private static final Color COLOR_MASUK    = new Color(39, 174, 96);
    private static final Color COLOR_KELUAR   = new Color(231, 76, 60);

    public MainApp() {
        setTitle("📁 Aplikasi Manajemen Surat - UAS PBO");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(COLOR_BG);

        initUI();
        refreshTable(manager.getDaftarSurat());
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lblTitle = new JLabel("📁  Aplikasi Manajemen Surat");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("UAS Pemrograman Berorientasi Objek");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(180, 210, 240));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        header.add(titlePanel, BorderLayout.WEST);

        // Stats
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        statsPanel.setOpaque(false);
        header.add(statsPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ── TOOLBAR ─────────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        // Search
        JLabel lblSearch = new JLabel("🔍 Cari:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        // Filter
        JLabel lblFilter = new JLabel("  Filter:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFilter = new JComboBox<>(new String[]{"Semua", "Surat Masuk", "Surat Keluar"});
        cbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Sort
        JLabel lblSort = new JLabel("  Urut:");
        lblSort.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbSort = new JComboBox<>(new String[]{"Terbaru", "Terlama"});
        cbSort.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Buttons
        JButton btnTambah  = buatTombol("＋ Tambah Surat", COLOR_PRIMARY);
        JButton btnEdit    = buatTombol("✏ Edit", new Color(243, 156, 18));
        JButton btnHapus   = buatTombol("🗑 Hapus", COLOR_DANGER);
        JButton btnExport  = buatTombol("📄 Export PDF", COLOR_SUCCESS);

        toolbar.add(lblSearch); toolbar.add(txtSearch);
        toolbar.add(lblFilter); toolbar.add(cbFilter);
        toolbar.add(lblSort);   toolbar.add(cbSort);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(btnTambah); toolbar.add(btnEdit);
        toolbar.add(btnHapus);  toolbar.add(btnExport);

        add(toolbar, BorderLayout.AFTER_LAST_LINE);

        // ── TABLE ───────────────────────────────────────────────────────────
        String[] kolom = {"No", "Nomor Surat", "Perihal", "Pengirim/Penerima", "Tanggal", "Jenis", "Keterangan"};
        tableModel = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(210, 230, 250));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Lebar kolom
        int[] widths = {40, 130, 200, 160, 90, 110, 180};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Warna baris berdasarkan jenis
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String jenis = (String) t.getValueAt(row, 5);
                    if ("Surat Masuk".equals(jenis)) {
                        c.setBackground(new Color(240, 255, 245));
                    } else {
                        c.setBackground(new Color(255, 242, 242));
                    }
                    if (col == 5) {
                        ((JLabel)c).setForeground("Surat Masuk".equals(jenis) ? COLOR_MASUK : COLOR_KELUAR);
                        ((JLabel)c).setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        ((JLabel)c).setForeground(Color.DARK_GRAY);
                        ((JLabel)c).setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 12));
        add(scroll, BorderLayout.CENTER);

        // ── STATUS BAR ──────────────────────────────────────────────────────
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        statusBar.setBackground(new Color(236, 240, 241));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        JLabel lblStatus = new JLabel("Siap");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);
        statusBar.add(lblStatus);
        add(statusBar, BorderLayout.SOUTH);

        // ── EVENTS ──────────────────────────────────────────────────────────
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { applyFilterSort(); }
        });
        cbFilter.addActionListener(e -> applyFilterSort());
        cbSort.addActionListener(e -> applyFilterSort());

        btnTambah.addActionListener(e -> showFormDialog(null, -1));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih surat yang ingin diedit!", "Perhatian", JOptionPane.WARNING_MESSAGE); return; }
            showFormDialog(currentList.get(row), row);
        });
        btnHapus.addActionListener(e -> hapusSurat());
        btnExport.addActionListener(e -> exportPdf());
    }

    // ── FORM DIALOG ─────────────────────────────────────────────────────────

    private void showFormDialog(Surat existing, int index) {
        JDialog dialog = new JDialog(this, existing == null ? "Tambah Surat" : "Edit Surat", true);
        dialog.setSize(460, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        form.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fNomor      = new JTextField(existing != null ? existing.getNomorSurat() : "");
        JTextField fPerihal    = new JTextField(existing != null ? existing.getPerihal() : "");
        JTextField fPengirim   = new JTextField(existing != null ? existing.getPengirimPenerima() : "");
        JTextField fTanggal    = new JTextField(existing != null ? existing.getTanggalFormatted() : LocalDate.now().format(FMT));
        JComboBox<String> fJenis = new JComboBox<>(new String[]{"Surat Masuk", "Surat Keluar"});
        if (existing != null && existing.getJenis() == Surat.JenisSurat.KELUAR) fJenis.setSelectedIndex(1);
        JTextField fKeterangan = new JTextField(existing != null ? existing.getKeterangan() : "");

        String[][] fields = {
            {"Nomor Surat:", ""}, {"Perihal:", ""}, {"Pengirim/Penerima:", ""},
            {"Tanggal (dd/MM/yyyy):", ""}, {"Jenis:", ""}, {"Keterangan:", ""}
        };
        JComponent[] inputs = {fNomor, fPerihal, fPengirim, fTanggal, fJenis, fKeterangan};

        for (int i = 0; i < fields.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.3;
            JLabel lbl = new JLabel(fields[i][0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            form.add(lbl, g);
            g.gridx = 1; g.weightx = 0.7;
            inputs[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            form.add(inputs[i], g);
        }

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        JButton btnSave   = buatTombol("💾 Simpan", COLOR_PRIMARY);
        JButton btnCancel = buatTombol("Batal", Color.GRAY);
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String nomor    = fNomor.getText().trim();
                String perihal  = fPerihal.getText().trim();
                String pengirim = fPengirim.getText().trim();
                String ket      = fKeterangan.getText().trim();
                if (nomor.isEmpty() || perihal.isEmpty() || pengirim.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Nomor, Perihal, dan Pengirim/Penerima wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDate tgl = LocalDate.parse(fTanggal.getText().trim(), FMT);
                Surat.JenisSurat jenis = fJenis.getSelectedIndex() == 0 ? Surat.JenisSurat.MASUK : Surat.JenisSurat.KELUAR;
                Surat surat = new Surat(nomor, perihal, pengirim, tgl, jenis, ket);
                if (existing == null) {
                    manager.tambahSurat(surat);
                } else {
                    int realIndex = manager.getDaftarSurat().indexOf(currentList.get(index));
                    manager.updateSurat(realIndex, surat);
                }
                applyFilterSort();
                dialog.dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Format tanggal salah! Gunakan dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    // ── HAPUS ───────────────────────────────────────────────────────────────

    private void hapusSurat() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih surat yang ingin dihapus!", "Perhatian", JOptionPane.WARNING_MESSAGE); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus surat \"" + currentList.get(row).getPerihal() + "\"?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            int realIndex = manager.getDaftarSurat().indexOf(currentList.get(row));
            manager.hapusSurat(realIndex);
            applyFilterSort();
        }
    }

    // ── EXPORT PDF ──────────────────────────────────────────────────────────

    private void exportPdf() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Laporan");
        fc.setSelectedFile(new java.io.File("laporan_surat.pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                manager.exportToPdf(currentList, fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                    "Laporan berhasil dibuka di browser!\nKamu bisa Print → Save as PDF dari browser.",
                    "Export Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── FILTER & SORT ───────────────────────────────────────────────────────

    private void applyFilterSort() {
        String keyword = txtSearch.getText().trim();
        int filterIdx  = cbFilter.getSelectedIndex();
        Surat.JenisSurat jenis = filterIdx == 1 ? Surat.JenisSurat.MASUK
                               : filterIdx == 2 ? Surat.JenisSurat.KELUAR : null;
        List<Surat> filtered = manager.filter(keyword, jenis);
        boolean asc = cbSort.getSelectedIndex() == 1;
        List<Surat> sorted = manager.sortByTanggal(filtered, asc);
        refreshTable(sorted);
    }

    private void refreshTable(List<Surat> list) {
        currentList = list;
        tableModel.setRowCount(0);
        int no = 1;
        for (Surat s : list) {
            tableModel.addRow(new Object[]{
                no++,
                s.getNomorSurat(),
                s.getPerihal(),
                s.getPengirimPenerima(),
                s.getTanggalFormatted(),
                s.getJenisString(),
                s.getKeterangan()
            });
        }
    }

    // ── HELPER ──────────────────────────────────────────────────────────────

    private JButton buatTombol(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}
