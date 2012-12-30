package net.freehal.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.freehal.core.logs.receiver.LogDestination;
import net.freehal.core.logs.receiver.StackTraceUtils;
import net.freehal.core.util.ExitListener;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.SystemUtils;

public class SwingLogWindow extends JFrame implements LogDestination, ExitListener {

	public static final Color GREEN = new Color(0x8ae25a);
	public static final Color BLUE = new Color(0x729fd2);
	public static final Color RED = new Color(0xf22929);
	public static final Color YELLOW = new Color(0xfce342);

	private static final long serialVersionUID = -1937995417483545737L;

	private LogTableModel model;

	public SwingLogWindow() {
		super("Freehal Logs");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			LogUtils.e(ex);
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SystemUtils.destructOnExit(this);

		final JTable table = new JTable();
		model = new LogTableModel(table);
		table.setModel(model);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		table.addComponentListener(new ColumnsAutoSizerListener(table));

		for (int i = 0; i < 3; i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			col.setCellRenderer(new MonospaceCellRenderer());
		}

		setLayout(new BorderLayout());
		add(scrollPane);
		pack();
		setSize(800, 500);
		setVisible(true);
	}

	@Override
	public void addLine(String type, String line, StackTraceElement stacktrace) {
		String[] entry = new String[] { StackTraceUtils.whereInCode(stacktrace).intern(), type, line.intern() };
		model.addEntry(entry);
	}

	@Override
	public void flush() {}

	public static class LogTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1207256428202734333L;
		private JTable table;
		private List<String[]> matrix = new ArrayList<String[]>();
		private long timeLastScroll = 0;
		final String columnNames[] = { "Source", "Type", "Message" };

		public LogTableModel(JTable table) {
			this.table = table;
		}

		public void addEntry(String[] entry) {
			matrix.add(entry);
			this.fireTableRowsInserted(matrix.size() - 1, matrix.size() - 1);
			scrollToBottom(table);
		}

		public void scrollToBottom(final JTable table) {
			long time = System.currentTimeMillis();
			if (time - timeLastScroll > 500) {
				timeLastScroll = time;

				try {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							table.scrollRectToVisible(table.getCellRect(matrix.size() - 1, 0, false));
							ColumnsAutoSizer.sizeColumnsToFit(table);
						}
					});
				} catch (Exception ex) {
					LogUtils.e(ex);
				}
			}
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			return matrix.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return matrix.get(rowIndex)[columnIndex];
		}

	}

	private static class ColumnsAutoSizerListener implements ComponentListener {
		private JTable table;

		public ColumnsAutoSizerListener(JTable table) {
			this.table = table;
		}

		public void componentResized(ComponentEvent e) {
			ColumnsAutoSizer.sizeColumnsToFit(table);
		}

		@Override
		public void componentHidden(ComponentEvent arg0) {}

		@Override
		public void componentMoved(ComponentEvent arg0) {}

		@Override
		public void componentShown(ComponentEvent arg0) {
			ColumnsAutoSizer.sizeColumnsToFit(table);
		}
	}

	private static class MonospaceCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 7945675681495781375L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);

			int mod = 0;
			if (column == 1) {
				Color color = Color.black;
				if ("error".equals(value))
					color = SwingLogWindow.RED;
				else if ("info".equals(value))
					color = SwingLogWindow.GREEN;
				else if ("debug".equals(value))
					color = SwingLogWindow.BLUE;
				else if ("warn".equals(value))
					color = SwingLogWindow.YELLOW;
				else if ("info".equals(value))
					color = SwingLogWindow.GREEN;
				comp.setForeground(color);
				mod = Font.BOLD;
			}
			comp.setFont(new Font(Font.MONOSPACED, mod, 11));
			return comp;
		}
	}

	@Override
	public void onExit(int status) {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
