package com.danylo.analyzer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Analyzer {
  private static String CHARSET_NAME = "utf8";

  public static void main(String[] args) {
    if (args.length < 2) {
      System.err.println("Enter origin and sample file path in arguments");
    }
    String originFilePath = args[0];
    String otherSampleFilePath = args[1];
    String buttonId = "make-everything-ok-button";
    if (args.length == 3) {
      buttonId = args[2];
    }

    Optional<Element> originButton = findElementById(new File(originFilePath), buttonId);
    if (!originButton.isPresent()) {
      System.err.println("Error reading origin file");
      System.exit(1);
    }

    System.out.println("Path to button in origin file:");
    System.out.println(getPath(originButton.get()));

    Optional<Element> sampleButton = findElementByOrigin(new File(otherSampleFilePath), originButton.get());
    if (!sampleButton.isPresent()) {
      System.err.println("Error reading sample file");
      System.exit(1);
    }

    System.out.println();
    System.out.println("Path to button in sample file:");
    System.out.println(getPath(sampleButton.get()));
  }

  private static Optional<Element> findElementById(File htmlFile, String targetElementId) {
    try {
      Document doc = Jsoup.parse(
          htmlFile,
          CHARSET_NAME,
          htmlFile.getAbsolutePath());

      return Optional.of(doc.getElementById(targetElementId));

    } catch (IOException e) {
      return Optional.empty();
    }
  }

  private static Optional<Element> findElementByOrigin(File htmlFile, Element origin) {
    StringBuilder classesQuery = new StringBuilder(origin.tagName()).append(".");
    String classNames = origin.classNames().stream().collect(Collectors.joining("."));
    classesQuery.append(classNames);
    String containerQuery = "div:contains(Make Everything OK Area)";
    StringBuilder nameQuery = new StringBuilder(origin.tagName())
        .append(":contains(")
        .append(origin.text())
        .append(")");
    try {
      Document doc = Jsoup.parse(
          htmlFile,
          CHARSET_NAME,
          htmlFile.getAbsolutePath());
      Element container = doc.select(containerQuery).last().parent();
      Elements byName = container.select(nameQuery.toString());
      if (!byName.isEmpty()) {
        return Optional.of(byName.last());
      }
      return Optional.of(container.select(classesQuery.toString()).first());
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  private static String getPath(Element element) {
    List<String> pathNodes = new ArrayList<>();
    element.parents().stream()
        .map(Analyzer::getTagNameWithOptionalSiblingIndex)
        .collect(Collectors.toCollection(LinkedList::new))
        .descendingIterator()
        .forEachRemaining(pathNodes::add);
    pathNodes.add(element.tagName());
    return pathNodes.stream().collect(Collectors.joining(" > "));
  }

  private static String getTagNameWithOptionalSiblingIndex(Element element) {
    if (element.siblingElements().isEmpty()) {
      return element.tagName();
    } else {
      return new StringBuilder(element.tagName())
          .append("[")
          .append(element.siblingIndex())
          .append("]")
          .toString();
    }
  }

}
