
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class QiitaConverter {

  static Charset UTF8 = Charset.forName("UTF8");
  static HashSet<Report> reports = new HashSet<>();

  public static void main(String[] args) throws Exception {
    String path = args[0];
    String output = args[1];

    List<String> urls = Files.readAllLines(Paths.get(path), UTF8);
    for (String url : urls) {
      if (url.contains("http")) {
        writeReqport(output, url);
      }
    }

    writeReadme(output);
    writeSummary(output);
  }

  static void writeReadme(String output) throws IOException {
    Path base = Paths.get(output);
    Path file = Paths.get(output, "README.md");
    
    Files.createDirectories(base);
    
    try (BufferedWriter w = Files.newBufferedWriter(file, UTF8)) {
      w.write("# まえがき");
      w.newLine();
    }
  }

  static void writeReqport(String output, String url) throws IOException {
    String file = url.substring(url.lastIndexOf('/') + 1) + ".md";

    
    try (BufferedReader reader = HttpRequest.get(url + ".md").followRedirects(true).bufferedReader("UTF-8")) {
      String line = reader.readLine();
      line = reader.readLine();
      String titleLine = line; // 2行目をタイトルとする
      if(!titleLine.startsWith("title: ")) {
        System.out.println("skip: " + url);
        return;
      }
      try (BufferedWriter w = Files.newBufferedWriter(Paths.get(output, file), UTF8)) {
        while (line != null) {
          w.write(line);
          w.newLine();
          line = reader.readLine();
        }
      }
      reports.add(new Report(titleLine.substring(7), file));
    }
  }

  static void writeSummary(String output) throws IOException {
    Path base = Paths.get(output);
    Path file = Paths.get(output, "SUMMARY.md");
    
    Files.createDirectories(base);
    
    try (BufferedWriter w = Files.newBufferedWriter(file, UTF8)) {
      w.write("# 目次");
      w.newLine();
      for (Report report : reports) {
        w.write(String.format("* [%s](%s)", report.title, report.file));
        w.newLine();
      }
    }
  }

  static class Report {

    public final String title;
    public final String file;

    public Report(String title, String file) {
      this.title = title;
      this.file = file;
    }
  }
}
