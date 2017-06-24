package me.ialistannen.javadocbot.javadoc.parsing;

import java.util.ArrayList;
import java.util.List;
import me.ialistannen.javadocbot.javadoc.model.Package;
import me.ialistannen.javadocbot.util.JsoupUtil;
import me.ialistannen.javadocbot.util.LinkUtil;
import me.ialistannen.javadocbot.util.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Finds all {@link Package}s.
 */
public class AllPackageParser {

  private static final String APPENDIX = "/overview-summary.html";

  private PackageParser packageParser;

  public AllPackageParser(PackageParser packageParser) {
    this.packageParser = packageParser;
  }

  /**
   * Parses the given URL and returns all classes found in the javadoc
   *
   * @param baseUrl The base URL for the javadoc
   * @return All class names with their link
   */
  public List<Package> parse(String baseUrl) {
    Document document = JsoupUtil
        .parseUrl(LinkUtil.concatLinksIgnoreDoubleSlash(baseUrl, APPENDIX));

    List<Package> packages = new ArrayList<>();

    document.getElementsByTag("table").stream()
        .filter(element -> element.hasClass("overviewSummary"))
        .flatMap(element -> element.getElementsByTag("tbody").stream())
        .forEach(tbody -> {
          for (Element row : tbody.getElementsByTag("tr")) {
            if (row.getElementsByTag("th").isEmpty()) {
              packages.add(getPackageFromRow(row));
            }
          }
        });

    return packages;
  }

  /**
   * Builds a {@link Package} from the row data.
   *
   * @param row The row to get it from
   * @return A {@link Package} built from the data of the row
   */
  private Package getPackageFromRow(Element row) {
    Pair<String, String> nameAndUrl = extractNameAndUrlFromRow(row);
    String shortDescription = extractShortDescriptionFromRow(row);

    return new Package(
        nameAndUrl.getKey(), nameAndUrl.getValue(), shortDescription, packageParser
    );
  }

  /**
   * @param row The row to get it from
   * @return A pair with the name (key) and url (value)
   */
  private Pair<String, String> extractNameAndUrlFromRow(Element row) {
    Element td = row.getElementsByTag("td").get(0);
    Element a = td.getElementsByTag("a").get(0);

    String name = a.text();
    String url = a.absUrl("href");

    return new Pair<>(name, url);
  }

  /**
   * @param row The row to get it from
   * @return The short description (or an empty string)
   */
  private String extractShortDescriptionFromRow(Element row) {
    Element td = row.getElementsByTag("td").get(1);

    return td.text();
  }
}
