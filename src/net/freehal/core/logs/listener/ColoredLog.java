package net.freehal.core.logs.listener;

import java.util.HashMap;
import java.util.Map;

import net.freehal.core.logs.output.LogOutput;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;

public class ColoredLog implements LogDestination {

	public static final ColorImpl ANSI = new AnsiColorImpl();
	public static final ColorImpl HTML = new HtmlColorImpl();

	private LogOutput stream;
	private ColorImpl colors;

	public ColoredLog(LogOutput stream, ColorImpl colors) {
		this.stream = stream;
		this.colors = colors;
	}

	private Color typeToColor(String type) {
		if (type.equals("error"))
			return Color.RED;
		else if (type.equals("info"))
			return Color.GREEN;
		else if (type.equals("debug"))
			return Color.BLUE;
		else if (type.equals("warn"))
			return Color.YELLOW;
		else if (type.equals("info"))
			return Color.GREEN;
		else
			return Color.NONE;
	}

	private String formatLine(String line) {
		line = RegexUtils.replace(line, "([\\[][a-zA-Z0-9 _-]+[\\]])",
				colors.color("$1", Color.YELLOW, Modifier.BOLD));
		line = RegexUtils.replace(line, "([\"][^\"]+[\"])", colors.color("$1", Color.YELLOW));
		line = RegexUtils.replace(line, "([\'][^\']+[\'])", colors.color("$1", Color.BLUE));
		line = RegexUtils.replace(line, "[{]([^\']+)[}]", "{" + colors.color("$1", Color.BLUE) + "}");
		return line;
	}

	@Override
	public void addLine(String type, String line, StackTraceElement stacktrace) {
		final String prefix = StringUtils.replace(StackTraceUtils.whereInCode(stacktrace), ":", ":")
				+ colors.color("[" + type + "] ", typeToColor(type), Modifier.BOLD)
				+ (type.length() == 4 ? " " : "");

		if (type.equals("error") || type.equals("warn"))
			line = colors.color(line, Color.RED, Modifier.BOLD);
		else
			line = formatLine(line);
		// use carriage return for output
		if (line.contains("\\r")) {
			line = RegexUtils.replace(line, "\\\\r", "\r" + prefix);
			stream.changeLast(prefix + line);
		} else {
			stream.append(prefix + line);
		}
	}

	@Override
	public void flush() {
		stream.flush();
	}

	public static enum Color {
		BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE, NONE;
	}

	public static enum Modifier {
		BOLD;
	}

	public static interface ColorImpl {
		String color(String text, Color color);

		String color(String text, Color color, Modifier mod);
	}

	public static class AnsiColorImpl implements ColorImpl {

		protected final String ANSI = "\u001B[";
		protected final String RESET = "0m";

		private Map<Color, String> colormap;
		private Map<Modifier, String> modemap;

		public AnsiColorImpl() {
			colormap = new HashMap<Color, String>();
			colormap.put(Color.BLACK, "30m");
			colormap.put(Color.NONE, "0m");
			colormap.put(Color.RED, "31m");
			colormap.put(Color.GREEN, "32m");
			colormap.put(Color.YELLOW, "33m");
			colormap.put(Color.BLUE, "34m");
			colormap.put(Color.PURPLE, "35m");
			colormap.put(Color.CYAN, "36m");
			colormap.put(Color.WHITE, "37m");

			modemap = new HashMap<Modifier, String>();
			modemap.put(Modifier.BOLD, "1;");
		}

		@Override
		public String color(String text, Color color) {
			return ANSI + colormap.get(color) + text + ANSI + RESET;
		}

		@Override
		public String color(String text, Color color, Modifier mod) {
			return ANSI + modemap.get(mod) + colormap.get(color) + text + ANSI + RESET;
		}

	}

	public static class HtmlColorImpl implements ColorImpl {

		private Map<Color, String> colormap;
		private Map<Modifier, String> modemap;

		public HtmlColorImpl() {
			colormap = new HashMap<Color, String>();
			colormap.put(Color.BLACK, "black");
			colormap.put(Color.NONE, "none");
			colormap.put(Color.RED, "red");
			colormap.put(Color.GREEN, "green");
			colormap.put(Color.YELLOW, "yellow");
			colormap.put(Color.BLUE, "blue");
			colormap.put(Color.PURPLE, "#800080");
			colormap.put(Color.CYAN, "cyan");
			colormap.put(Color.WHITE, "white");

			modemap = new HashMap<Modifier, String>();
			modemap.put(Modifier.BOLD, "b");
		}

		@Override
		public String color(String text, Color color) {
			return "<span style='color: " + colormap.get(color) + "'>" + text + "</span>";
		}

		@Override
		public String color(String text, Color color, Modifier mod) {
			return "<" + modemap.get(mod) + "><span style='color: " + colormap.get(color) + "'>" + text
					+ "</span></" + modemap.get(mod) + ">";
		}

	}
}
